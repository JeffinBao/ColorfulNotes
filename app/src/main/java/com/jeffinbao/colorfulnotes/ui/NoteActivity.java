package com.jeffinbao.colorfulnotes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.event.NoteBookEvent;
import com.jeffinbao.colorfulnotes.event.NoteEvent;
import com.jeffinbao.colorfulnotes.evernote.Evernote;
import com.jeffinbao.colorfulnotes.evernote.task.EvernoteSyncTask;
import com.jeffinbao.colorfulnotes.model.Note;
import com.jeffinbao.colorfulnotes.model.NoteBook;
import com.jeffinbao.colorfulnotes.ui.fragment.ChooseNotebookDialogFragment;
import com.jeffinbao.colorfulnotes.ui.fragment.NoteDetailDialogFragment;
import com.jeffinbao.colorfulnotes.utils.OSUtil;
import com.jeffinbao.colorfulnotes.utils.SoftKeyboardUtil;
import com.jeffinbao.colorfulnotes.utils.StringUtil;
import com.jeffinbao.colorfulnotes.utils.TimeUtil;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Author: baojianfeng
 * Date: 2015-10-25
 * Role: exact note details
 */
public class NoteActivity extends BaseActivity implements View.OnClickListener,
        ChooseNotebookDialogFragment.NoteBookChangeListener, AMapLocationListener,
        NoteDetailDialogFragment.LocationReturnSuccessListener {
    public static final String TAG = "NoteActivity";

    private NoteStatus noteStatus = NoteStatus.VIEW_NOTE;
    private NoteEvent.NoteAction noteAction;
    private EditText noteTitle;
    private TextView noteBookTextView;
    private EditText noteContent;
    private FloatingActionButton noteFab;
    private Intent intent;
    private Note note;
    private FinalDb db;
    private MenuItem menuItemDone;
    private boolean isFirstIn = true;
    private String originNotebookName;
    private String notebookName;
    private Evernote evernote;
    private AlertDialog syncProgressDialog;

    private int originalNoteBookPosition;
    private int noteBookPosition;
    private int notePosition;
    private List<NoteBook> changedNoteBookList;
    private Map<NoteBook, Integer> changedNoteBookPositionMap;

    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationClientOption;
    private String createLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != menuItemDone && menuItemDone.isVisible()) {
                    showNoteNotSaveDialog();
                } else {
                    onBackPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NoteActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoteActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note;
    }

    @Override
    protected void initViews() {
        noteTitle = (EditText) findViewById(R.id.exact_note_title);
        noteBookTextView = (TextView) findViewById(R.id.exact_note_book);
        noteContent = (EditText) findViewById(R.id.exact_note_content);
        noteFab = (FloatingActionButton) findViewById(R.id.exact_note_fab);

        db = FinalDb.create(this, NConstants.NOTE_DB_NAME);

        noteBookTextView.setOnClickListener(this);
        noteFab.setOnClickListener(this);
        noteTitle.addTextChangedListener(new NoteTextWatcher());
        noteContent.addTextChangedListener(new NoteTextWatcher());

        syncProgressDialog = new AlertDialog.Builder(this)
                .setView(R.layout.progressbar_layout)
                .setMessage(R.string.evernote_sync_now)
                .setCancelable(false)
                .create();
    }

    @Override
    protected void initValues() {
        intent = getIntent();
        if (intent.hasExtra(NConstants.NOTE_TYPE)) {
            noteStatus = (NoteStatus) intent.getSerializableExtra(NConstants.NOTE_TYPE);
        }
        if (intent.hasExtra(NConstants.NOTE_BOOK_POSITION)) {
            originalNoteBookPosition = intent.getIntExtra(NConstants.NOTE_BOOK_POSITION, -1);
            noteBookPosition = originalNoteBookPosition;
        }
        if (intent.hasExtra(NConstants.NOTE_POSITION)) {
            notePosition = intent.getIntExtra(NConstants.NOTE_POSITION, -1);
        }

        initNoteView(noteStatus);

        changedNoteBookList = new ArrayList<>();
        changedNoteBookPositionMap = new HashMap<>();
        evernote = new Evernote(this, db);

        startGetLocationTask();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.exact_note_book: {
                if (noteStatus == NoteStatus.VIEW_NOTE) {
                    return;
                }
                ArrayList<String> noteBookNamesList = new ArrayList<String>();
                List<NoteBook> list = db.findAll(NoteBook.class);
                for (int i = 0; i < list.size(); i++) {
                    noteBookNamesList.add(list.get(i).getName());
                }
                showChooseNotebookFrgment(noteBookNamesList, notebookName);
                break;
            }
            case R.id.exact_note_fab: {
                noteStatus = NoteStatus.EDIT_NOTE;
                SoftKeyboardUtil.showKeyboard(NoteActivity.this);
                noteFab.setVisibility(View.GONE);
                toggleEditText(true);
                getActionBarToolbar().setTitle(R.string.note_edit);
                noteTitle.setSelection(note.getTitle().length());
                noteContent.setSelection(note.getContent().length());
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemDone = menu.getItem(0);
        if (isFirstIn) {
            menuItemDone.setVisible(false);
            isFirstIn = false;
        }

        if (noteStatus == NoteStatus.CREATE_NOTE) {
            menu.getItem(1).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.note_activity_done: {
                saveNote();
                return true;
            }
            case R.id.note_activity_sync_note: {
                syncNote(evernote, note);
                return true;
            }
            case R.id.note_activity_detail: {
                showNoteDetailFragment(note);
                return true;
            }
            case R.id.note_activity_delete: {
                showDeleteNoteDialog();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (null != menuItemDone && menuItemDone.isVisible()) {
                showNoteNotSaveDialog();
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private void initNoteView(NoteStatus status) {
        switch (status) {
            case VIEW_NOTE: {
                getActionBarToolbar().setTitle(R.string.note_view);
                if (intent.hasExtra(NConstants.COLOR)) {
                    int color = intent.getIntExtra(NConstants.COLOR, 0);
                    if (color != 0) {
                        getActionBarToolbar().setBackgroundColor(color);
                        noteFab.setBackgroundTintList(ColorStateList.valueOf(color));
                    }
                }
                if (intent.hasExtra(NConstants.NOTE_ID)) {
                    note = db.findById(intent.getIntExtra(NConstants.NOTE_ID, 0), Note.class);
                    noteTitle.setText(note.getTitle());
                    displayNotebookName(noteBookTextView, note.getNoteBookName(), NConstants.NOTEBOOK_TITLE_THRESHOLD);
                    noteContent.setText(note.getContent());
                    originNotebookName = note.getNoteBookName();
                    notebookName = note.getNoteBookName();
                }
                break;
            }
            case EDIT_NOTE: {
                noteFab.setVisibility(View.GONE);
                toggleEditText(true);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                getActionBarToolbar().setTitle(R.string.note_edit);
                if (intent.hasExtra(NConstants.COLOR)) {
                    int color = intent.getIntExtra(NConstants.COLOR, 0);
                    if (color != 0) {
                        getActionBarToolbar().setBackgroundColor(color);
                    }
                }
                if (intent.hasExtra(NConstants.NOTE_ID)) {
                    note = db.findById(intent.getIntExtra(NConstants.NOTE_ID, 0), Note.class);
                    noteTitle.setText(note.getTitle());
                    displayNotebookName(noteBookTextView, note.getNoteBookName(), NConstants.NOTEBOOK_TITLE_THRESHOLD);
                    noteContent.setText(note.getContent());
                    noteTitle.setSelection(note.getTitle().length());
                    noteContent.setSelection(note.getContent().length());
                    originNotebookName = note.getNoteBookName();
                    notebookName = originNotebookName;
                }
                break;
            }
            case CREATE_NOTE: {
                note = new Note();
                noteFab.setVisibility(View.GONE);
                toggleEditText(true);
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                getActionBarToolbar().setTitle(R.string.note_create);
                if (intent.hasExtra(NConstants.NOTE_BOOK_NAME)) {
                    originNotebookName = intent.getStringExtra(NConstants.NOTE_BOOK_NAME);
                    notebookName = originNotebookName;
                    displayNotebookName(noteBookTextView, intent.getStringExtra(NConstants.NOTE_BOOK_NAME), NConstants.NOTEBOOK_TITLE_THRESHOLD);
                } else {
                    notebookName = getString(R.string.default_note_book);
                    noteBookTextView.setText(R.string.default_note_book);
                }
                break;
            }
        }
    }

    public void displayNotebookName(TextView noteBookTextView, String s, int threshold) {
        if (null == noteBookTextView) {
            return;
        }

        int titleChineseStringCount = StringUtil.getChineseStringCountWithThreshold(s, threshold);

        if (StringUtil.getStringCharacterCount(s) <= threshold) {
            noteBookTextView.setText(s);
        } else {
            noteBookTextView.setText(s.substring(0, NConstants.NOTEBOOK_TITLE_THRESHOLD - titleChineseStringCount) + NConstants.ELLIPSIS);
        }
    }

    // control 2 editTexts state
    private void toggleEditText(boolean isEnable) {
        if (isEnable) {
            noteContent.requestFocus();
            noteContent.setEnabled(true);
            noteTitle.setEnabled(true);
        } else {
            noteContent.setEnabled(false);
            noteTitle.setEnabled(false);
        }
    }

    private void saveNote() {
        SoftKeyboardUtil.hideKeyboard(this);
        menuItemDone.setVisible(false);
        toggleEditText(false);
        changedNoteBookList.clear();
        changedNoteBookPositionMap.clear();

        if (!isEditTextEmpty(noteTitle)) {
            note.setTitle(noteTitle.getText().toString());
        } else {
            if (noteContent.getText().toString().length() <= 20) {
                note.setTitle(noteContent.getText().toString());
            } else {
                note.setTitle(noteContent.getText().toString().substring(0,21));
            }
        }

        if (!isEditTextEmpty(noteContent)) {
            note.setContent(noteContent.getText().toString());
        } else {
            note.setContent(noteTitle.getText().toString());
        }

        note.setNoteBookName(notebookName);
        note.setLastUpdateTime(TimeUtil.getCurrentTimeString());
        note.setLastUpdateTimeInLong(System.currentTimeMillis());
        switch (noteStatus) {
            case CREATE_NOTE: {
                noteAction = NoteEvent.NoteAction.CREATE;
                note.setCreateTime(TimeUtil.getCurrentTimeString());
                if (null != createLocation) {
                    note.setCreateLocation(createLocation);
                }
                db.saveBindId(note);

                updateNotebookCount(notebookName, noteBookPosition);
                EventBus.getDefault().post(new NoteEvent(note, notePosition, noteAction));
                break;
            }
            case EDIT_NOTE: {
                if (null == note.getCreateLocation() && null != createLocation) {
                    note.setCreateLocation(createLocation);
                }
                db.update(note);
                getActionBarToolbar().setTitle(R.string.note_view);
                noteFab.setVisibility(View.VISIBLE);

                if (null != originNotebookName && !originNotebookName.equals(notebookName)) {
                    noteAction = NoteEvent.NoteAction.CHANGE_NOTEBOOK;

                    //update count of exact notebook
                    updateNotebookCount(notebookName, noteBookPosition);
                    updateNotebookCount(originNotebookName, originalNoteBookPosition);

                    EventBus.getDefault().post(new NoteEvent(note, notePosition, noteAction));
                } else {
                    noteAction = NoteEvent.NoteAction.EDIT;
                    EventBus.getDefault().post(new NoteEvent(note, notePosition, noteAction));
                }

                break;
            }
        }

        originalNoteBookPosition = noteBookPosition;
        originNotebookName = notebookName;

        EventBus.getDefault().post(new NoteBookEvent(changedNoteBookList, changedNoteBookPositionMap, NoteBookEvent.NoteBookAction.UPDATE_NOTE_BOOK));

        finish();
    }

    private void updateNotebookCount(String name, int noteBookPosition) {
        if (null != db) {
            NoteBook noteBook = db.findAllByWhere(NoteBook.class, "name='" + name + "'").get(0);
            int currentCount = db.findAllByWhere(Note.class, "noteBookName='" + name + "'").size();
            noteBook.setCount(currentCount);
            db.update(noteBook);

            changedNoteBookList.add(noteBook);
            changedNoteBookPositionMap.put(noteBook, noteBookPosition);
        }
    }

    private void showDeleteNoteDialog() {
        SoftKeyboardUtil.hideKeyboard(this);

        changedNoteBookList.clear();
        changedNoteBookPositionMap.clear();

        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.note_delete_confirm)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noteAction = NoteEvent.NoteAction.DELETE;
                        db.delete(note);

                        //update count of exact notebook
                        updateNotebookCount(notebookName, noteBookPosition);
                        EventBus.getDefault().post(new NoteEvent(null, notePosition, noteAction));
                        EventBus.getDefault().post(new NoteBookEvent(changedNoteBookList, changedNoteBookPositionMap, NoteBookEvent.NoteBookAction.UPDATE_NOTE_BOOK));
                        finish();
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

    private void showNoteNotSaveDialog() {
        AlertDialog.Builder noteNotSaveDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.note_not_save_confirm)
                .setPositiveButton(R.string.give_up, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveNote();
                        finish();
                    }
                });

        AlertDialog dialog = noteNotSaveDialog.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideInAndOutAnimation;
        dialog.show();
    }

    private void showChooseNotebookFrgment(ArrayList<String> noteBookNamesList, String currentNoteBookName) {
        SoftKeyboardUtil.hideKeyboard(this);

        ChooseNotebookDialogFragment fragment = ChooseNotebookDialogFragment.getInstance(noteBookNamesList, currentNoteBookName);
        fragment.setNoteBookChangeListener(this);
        fragment.show(getFragmentManager(), "choose_note_book");

    }

    @Override
    public void onNoteBookChange(String name, int position) {
        notebookName = name;
        displayNotebookName(noteBookTextView, name, NConstants.NOTEBOOK_TITLE_THRESHOLD);
        noteBookPosition = position;

        String contentAll = noteContent.getText().toString();
        String titleAll = noteTitle.getText().toString();
        if (((null != originNotebookName && !originNotebookName.equals(notebookName)) ||
                (!TextUtils.equals(contentAll, note.getContent()) || !TextUtils.equals(titleAll, note.getTitle())))
                && (noteTitle.getText().length() > 0 || noteContent.getText().length() > 0)) {
            menuItemDone.setVisible(true);
        } else {
            menuItemDone.setVisible(false);
        }
    }

    private void syncNote(Evernote evernote, Note note) {
        if (null == evernote) {
            return;
        }

        if (!evernote.isLoggedIn()) {
            Toast.makeText(getApplicationContext(), R.string.evernote_please_bind, Toast.LENGTH_SHORT).show();
            return;
        }

        List<Note> notes = new ArrayList<Note>();
        notes.add(note);
        new EvernoteSyncTask(this, evernote).execute(notes);
    }

    @Subscribe (threadMode =  ThreadMode.MainThread)
    public void onUserEvent(EvernoteSyncTask.SyncState result) {
        switch (result) {
            case START: {
                syncProgressDialog.show();
                break;
            }
            case SUCCESS: {
                syncProgressDialog.dismiss();
                break;
            }
            case AUTH_EXPIRED: {
                syncProgressDialog.dismiss();
                break;
            }
            case PREMISSION_DENIED: {
                syncProgressDialog.dismiss();
                break;
            }
            case QUOTA_REACHED: {
                syncProgressDialog.dismiss();
                break;
            }
            case RATE_LIMIT_REACHED: {
                syncProgressDialog.dismiss();
                break;
            }
            case OTHER_ERROR: {
                syncProgressDialog.dismiss();
                break;
            }
        }
    }

    private void showNoteDetailFragment(Note note) {
        SoftKeyboardUtil.hideKeyboard(this);

        NoteDetailDialogFragment fragment = NoteDetailDialogFragment.getInstance(note);
        fragment.setLocationReturnSuccessListener(this);
        fragment.show(getFragmentManager(), "note_detail");
    }

    @Override
    public void onLocationSuccessListener(String location) {
        noteAction = NoteEvent.NoteAction.EDIT_ADD_LOCATION;

        note.setCreateLocation(location);
        note.setLastUpdateTime(TimeUtil.getCurrentTimeString());
        note.setLastUpdateTimeInLong(System.currentTimeMillis());
        db.update(note);

        EventBus.getDefault().post(new NoteEvent(note, notePosition, noteAction));
    }

    private boolean isEditTextEmpty(EditText editText) {
        String contentAll = editText.getText().toString();
        String content = contentAll.replaceAll("\\s*|\r|\t|\n", "");
        return TextUtils.isEmpty(content);
    }

    class NoteTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null == menuItemDone) {
                return;
            }

            String titleAll = noteTitle.getText().toString();
            String contentAll = noteContent.getText().toString();
            if (!isEditTextEmpty(noteTitle) || !isEditTextEmpty(noteContent)) {
                if (titleAll.length() > 20) {
                    Toast.makeText(NoteActivity.this, getString(R.string.note_title_length_exceed_20), Toast.LENGTH_SHORT).show();
                }

                if ((TextUtils.equals(contentAll, note.getContent()) && TextUtils.equals(titleAll, note.getTitle())) &&
                        (null != originNotebookName && originNotebookName.equals(notebookName))) {
                    menuItemDone.setVisible(false);
                    return;
                }
                menuItemDone.setVisible(true);
            } else {
                menuItemDone.setVisible(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void startGetLocationTask() {
        if (!OSUtil.isNetworkAvailable()) return;

        locationClient = new AMapLocationClient(getApplicationContext());
        locationClient.setLocationListener(this);

        locationClientOption = new AMapLocationClientOption();
        locationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        locationClientOption.setInterval(2000);

        locationClient.setLocationOption(locationClientOption);
        locationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (null != createLocation && null != locationClient) {
            locationClient.stopLocation();
            return;
        }

        if (null != aMapLocation) {
            if (aMapLocation.getErrorCode() == 0) {
                createLocation = aMapLocation.getCountry() + NConstants.DOT +
                        aMapLocation.getProvince() + NConstants.DOT +
                        aMapLocation.getCity() + NConstants.DOT +
                        aMapLocation.getDistrict();
                Log.d(TAG, "Location info: " + createLocation);
            }
        } else {
            Log.d(TAG, "location errcode: " + aMapLocation.getErrorCode() + ", errInfo: " + aMapLocation.getErrorInfo());
        }
    }

    public enum NoteStatus {
        VIEW_NOTE,
        EDIT_NOTE,
        CREATE_NOTE
    }

}
