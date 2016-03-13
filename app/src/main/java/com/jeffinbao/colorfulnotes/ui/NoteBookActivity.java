package com.jeffinbao.colorfulnotes.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.EvernoteSession;
import com.evernote.client.android.asyncclient.EvernoteCallback;
import com.evernote.edam.type.User;
import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.event.NoteBookEvent;
import com.jeffinbao.colorfulnotes.evernote.Evernote;
import com.jeffinbao.colorfulnotes.evernote.task.EvernoteSyncTask;
import com.jeffinbao.colorfulnotes.model.Note;
import com.jeffinbao.colorfulnotes.model.NoteBook;
import com.jeffinbao.colorfulnotes.ui.adapter.BaseListAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.BaseRecyclerViewAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.DrawerListAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.NoteBookAdapter;
import com.jeffinbao.colorfulnotes.ui.view.PasscodeView;
import com.jeffinbao.colorfulnotes.utils.DatabaseUtil;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;
import com.jeffinbao.colorfulnotes.utils.SoftKeyboardUtil;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Author: baojianfeng
 * Date: 2015-10-08
 * Role: NoteBookActivity as the MainActivity
 */
public class NoteBookActivity extends BaseActivity implements BaseRecyclerViewAdapter.onItemClickListener<NoteBook>,
        BaseListAdapter.OnItemClickListener, EvernoteCallback<User>, PasscodeView.PasscodeActionStatusListener {

    public static final String TAG = "NoteBookActivity";

    private static final int CLOSE_DRAWER = 0x001;

    private FinalDb db;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private LinearLayout drawerRootView;
    private RecyclerView noteBookRecyclerView;
    private TextView noteBookCount;
    private NoteBookAdapter noteBookAdapter;
    private List<NoteBook> noteBookList;
    private RecyclerView drawerRecyclerView;
    private AlertDialog syncProgressDialog;
    private Evernote evernote;
    private PreferenceUtil preferenceUtil;
    private PasscodeView passcodeView;
    private NoteBookHandler handler;

    private static class NoteBookHandler extends Handler {
        private WeakReference<NoteBookActivity> weakReference;

        public NoteBookHandler(NoteBookActivity activity) {
            weakReference = new WeakReference<NoteBookActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            NoteBookActivity activity = weakReference.get();
            if (null == activity) {
                return;
            }

            switch (msg.what) {
                case CLOSE_DRAWER: {
                    activity.openOrCloseDrawer();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.openActivityDurationTrack(false);
        AnalyticsConfig.enableEncrypt(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NoteBookActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoteBookActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_book;
    }

    @Override
    protected void initViews() {
        passcodeView = (PasscodeView) findViewById(R.id.passcode_view_in_note_book);
        passcodeView.setPasscodeActionType(NConstants.VALIDATE_PASSCODE);
        passcodeView.setPasscodeActionStatusListener(this);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerRootView = (LinearLayout) findViewById(R.id.left_drawer_layout);
        noteBookRecyclerView = (RecyclerView) findViewById(R.id.note_book_recycler_view);
        noteBookCount = (TextView) findViewById(R.id.note_book_count);
        drawerRecyclerView = (RecyclerView) findViewById(R.id.drawer_recycler_view);

        syncProgressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.progressbar_layout)
                .setMessage(R.string.evernote_sync_now)
                .setCancelable(false)
                .create();
    }

    @Override
    protected void initValues() {
        initDatabase();

        initDrawer();
        initNoteBookRecyclerView();

        evernote = new Evernote(this, db);
        preferenceUtil = PreferenceUtil.getDefault(this);
        handler = new NoteBookHandler(this);
        EventBus.getDefault().register(this);

        if (preferenceUtil.getBoolean(getString(R.string.preference_passcode_open_or_not))) {
            passcodeView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openOrCloseDrawer();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClickListener(View view, int position, NoteBook values) {
        switch (view.getId()) {
            case R.id.note_book_card_view: {
                Intent intent = new Intent(NoteBookActivity.this, NoteDisplayActivity.class);
                intent.putExtra(NConstants.NOTE_BOOK_NAME, values.getName());
                intent.putExtra(NConstants.NOTE_BOOK_POSITION, position);
                startActivity(intent);
                break;
            }
            case R.id.note_book_more: {
                showActionMoreMenu(view, position, values);
                break;
            }
        }

    }

    @Override
    public boolean onLongClickListener(View view, int position, NoteBook values) {
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_book_option, menu);
        return true;
    }

    @Subscribe (threadMode = ThreadMode.MainThread)
    public void onUserEvent(NoteBookEvent event) {
        List<NoteBook> list = event.getChangedNoteBookList();
        Map<NoteBook, Integer> map = event.getChangedNoteBookMap();

        switch (event.getNoteBookAction()) {
            case UPDATE_NOTE_BOOK: {
                for (NoteBook noteBook : list) {
                    int position = map.get(noteBook);
                    noteBookList.set(position, noteBook);
                }
                break;
            }
            case ADD_NEW_NOTE_BOOK: {
                NoteBook noteBook = list.get(0);
                int position = map.get(noteBook);
                noteBookList.add(position, noteBook);
                break;
            }
        }

        noteBookAdapter.notifyDataSetChanged();
    }

    @Subscribe (threadMode =  ThreadMode.MainThread)
    public void onUserEvent(EvernoteSyncTask.SyncState result) {
        switch (result) {
            case START: {
                syncProgressDialog.show();
                break;
            }
            case SUCCESS: {
                showToast(getString(R.string.evernote_sync_success));
                syncProgressDialog.dismiss();
                break;
            }
            case AUTH_EXPIRED: {
                showToast(getString(R.string.evernote_auth_expired));
                syncProgressDialog.dismiss();
                break;
            }
            case PREMISSION_DENIED: {
                showToast(getString(R.string.evernote_permission_denied));
                syncProgressDialog.dismiss();
                break;
            }
            case QUOTA_REACHED: {
                showToast(getString(R.string.evernote_quota_reached));
                syncProgressDialog.dismiss();
                break;
            }
            case RATE_LIMIT_REACHED: {
                showToast(getString(R.string.evernote_rate_limit_reached));
                syncProgressDialog.dismiss();
                break;
            }
            case OTHER_ERROR: {
                showToast(getString(R.string.evernote_sync_failed));
                syncProgressDialog.dismiss();
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.note_book_new: {
                showAddNewOrEditNoteBookDialog(NoteBookActivity.this, R.string.note_book_new, null, -1);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onItemClick(View view, int position, String noteBookName) {
        switch (position) {
            case 0: {
                if (null == evernote) {
                    return;
                }

                if (TextUtils.isEmpty(preferenceUtil.getString(getString(R.string.evernote_user_name)))) {
                    evernote.authLogin(NoteBookActivity.this);
                } else {
                    evernote.logout();

                    DrawerListAdapter.ViewHolder viewHolder = (DrawerListAdapter.ViewHolder) drawerRecyclerView.findViewHolderForAdapterPosition(0);
                    viewHolder.drawerItemName.setText(getResources().getStringArray(R.array.drawer_item_list)[0]);

                    preferenceUtil.putString(getString(R.string.evernote_user_name), "");
                }
                break;
            }
            case 1: {
                if (null == evernote) {
                    return;
                }

                if (!evernote.isLoggedIn()) {
                    Toast.makeText(getApplicationContext(), R.string.evernote_please_bind, Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Note> notes = db.findAll(Note.class);
                if (notes.size() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.evernote_at_least_one_note, Toast.LENGTH_SHORT).show();
                    return;
                }

                new EvernoteSyncTask(this, evernote).execute(notes);
                openOrCloseDrawer();
                break;
            }
            case 2: {
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                handler.sendEmptyMessageDelayed(CLOSE_DRAWER, 500);
                break;
            }
            case 3: {
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                handler.sendEmptyMessageDelayed(CLOSE_DRAWER, 500);
                break;
            }

        }
    }

    private void initDrawer() {
        List<String> drawerList = Arrays.asList(getResources().getStringArray(R.array.drawer_item_list));
        DrawerListAdapter adapter = new DrawerListAdapter(this, drawerList);
        adapter.setItemClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setLayoutManager(manager);
        drawerRecyclerView.setAdapter(adapter);

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, 0, 0){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerLayout.setDrawerListener(drawerToggle);
        drawerLayout.setScrimColor(getResources().getColor(R.color.black_transparency_12));
    }

    private void initNoteBookRecyclerView() {
        db = FinalDb.create(this, NConstants.NOTE_DB_NAME);

        noteBookList = initNoteBooksData();
        noteBookAdapter = new NoteBookAdapter(noteBookList, this);
        noteBookAdapter.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        noteBookRecyclerView.setHasFixedSize(true);
        noteBookRecyclerView.setLayoutManager(layoutManager);
        noteBookRecyclerView.setAdapter(noteBookAdapter);
    }

    private List<NoteBook> initNoteBooksData() {
        List<NoteBook> list;
        list = db.findAll(NoteBook.class);
        Log.d(TAG, "notebook list size: " + list.size());
        return list;
    }

    private void showActionMoreMenu(View view, final int position, final NoteBook values) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_note_book_more, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                switch (id) {
                    case R.id.menu_note_book_rename: {
                        showAddNewOrEditNoteBookDialog(NoteBookActivity.this, R.string.note_book_rename, values, position);
                        return true;
                    }
                    case R.id.menu_note_book_delete: {
                        showDeleteNoteBookDialog(position, values);
                        return true;
                    }

                    default: {
                        return false;
                    }
                }

            }
        });
        popupMenu.show();
    }

    private void showDeleteNoteBookDialog(final int position, final NoteBook values) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.note_book_delete_confirm)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(values);
                        if (values.getCount() > 0) {
                            db.deleteByWhere(Note.class, "noteBookName='" + values.getName() + "'");
                        }
                        Log.d(TAG, "position is: " + position);
                        noteBookList.remove(position);
                        noteBookAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog = deleteDialog.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideInAndOutAnimation;
        dialog.show();
    }

    private void showAddNewOrEditNoteBookDialog(final Context context, @StringRes int id, @Nullable final NoteBook values, final int position) {
        SoftKeyboardUtil.showKeyboard(context);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        View layout = getLayoutInflater().inflate(R.layout.note_book_new_or_edit_dialog, null);
        dialogBuilder.setView(layout);
        dialogBuilder.setMessage(id);
        final EditText inputName = (EditText) layout.findViewById(R.id.note_book_edittext_title);
        if (null != values) {
            inputName.setText(values.getName());
            inputName.setSelection(values.getName().length());
        }
        dialogBuilder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SoftKeyboardUtil.hideKeyboard(context, inputName);

                if (inputName.getText().length() > 0 && !isNoteBookNameExist(inputName.getText().toString(), values)) {
                    if (null != values) {
                        //rename notebook situation
                        if (!values.getName().equals(inputName.getText().toString())) {
                            values.setName(inputName.getText().toString());
                            db.update(values);

                            noteBookList.set(position, values);
                            noteBookAdapter.notifyDataSetChanged();
                        }
                    } else {
                        //create notebook situation
                        NoteBook noteBook = new NoteBook();
                        noteBook.setCount(0);
                        noteBook.setName(inputName.getText().toString());
                        db.saveBindId(noteBook);

                        noteBookList.add(noteBook);
                        noteBookAdapter.notifyDataSetChanged();
                    }

                } else if (inputName.getText().length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.note_book_name_should_not_be_null, Toast.LENGTH_SHORT).show();

                } else if (isNoteBookNameExist(inputName.getText().toString(), values)) {
                    Toast.makeText(getApplicationContext(), R.string.note_book_name_should_note_be_same, Toast.LENGTH_SHORT).show();
                }

            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SoftKeyboardUtil.hideKeyboard(context, inputName);
            }
        });

        dialogBuilder.show();
    }

    private void openOrCloseDrawer() {
        if (drawerLayout.isDrawerOpen(drawerRootView)) {
            drawerLayout.closeDrawer(drawerRootView);
        } else {
            drawerLayout.openDrawer(drawerRootView);
        }
    }

    private void copyDatabaseToPhone() {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        boolean dbExist = dbUtil.isDatabaseExist(NConstants.NOTE_DB_NAME);

        if (!dbExist) {
            dbUtil.copyDatabase(NConstants.NOTE_DB_NAME);
        }
    }

    private void initDatabase() {
        DatabaseUtil dbUtil = new DatabaseUtil(this);
        boolean dbExist = dbUtil.isDatabaseExist(NConstants.NOTE_DB_NAME);

        if (!dbExist) {
            FinalDb db = FinalDb.create(this, NConstants.NOTE_DB_NAME);

            String[] notebookNames = getResources().getStringArray(R.array.init_note_book_item_list);
            for (String name : notebookNames) {
                NoteBook book = new NoteBook();
                book.setCount(0);
                book.setName(name);
                db.saveBindId(book);
            }
        }
    }

    private boolean isNoteBookNameExist(String name, @Nullable NoteBook values) {
        List<NoteBook> list;

        if (null != values) {
            //rename notebook situation
            list = db.findAllByWhere(NoteBook.class, "name!='" + values.getName() + "'");
        } else {
            //create new notebook situation
            list = db.findAll(NoteBook.class);
        }

        for (int i = 0; i < list.size(); i++) {
            if (name.equals(list.get(i).getName())) {
                return true;
            }
        }

        return false;
    }

    private void showToast(@StringRes String message) {
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitSuccess() {

    }

    @Override
    public void onValidateSuccess() {
        runPasscodeViewExitAnimation();
    }

    @Override
    public void onChangeSuccess() {

    }

    @Override
    public void onPasscodeForget() {
        Intent intent = new Intent(this, SettingActivity.class);
        intent.putExtra(NConstants.PASSCODE_FORGET, true);
        startActivity(intent);
    }

    private void runPasscodeViewExitAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_out_bottom);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                passcodeView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        passcodeView.startAnimation(animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EvernoteSession.REQUEST_CODE_LOGIN) {
            if (resultCode == RESULT_OK) {
                evernote.getUser(this);
                Toast.makeText(getApplicationContext(), R.string.evernote_login_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.evernote_login_fail, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSuccess(User result) {
        if (null != result) {
            String userName = result.getUsername();
            DrawerListAdapter.ViewHolder viewHolder = (DrawerListAdapter.ViewHolder) drawerRecyclerView.findViewHolderForAdapterPosition(0);
            viewHolder.drawerItemName.setText(getString(R.string.evernote_unbind_hint, userName));

            preferenceUtil.putString(getString(R.string.evernote_user_name), userName);
        }
    }

    @Override
    public void onException(Exception exception) {
        Log.d(TAG, "get user exception: " + exception.toString());
    }
}
