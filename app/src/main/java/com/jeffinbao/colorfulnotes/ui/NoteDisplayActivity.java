package com.jeffinbao.colorfulnotes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.event.NoteBookEvent;
import com.jeffinbao.colorfulnotes.event.NoteEvent;
import com.jeffinbao.colorfulnotes.model.Note;
import com.jeffinbao.colorfulnotes.model.NoteBook;
import com.jeffinbao.colorfulnotes.ui.adapter.BaseRecyclerViewAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.NoteAdapter;
import com.jeffinbao.colorfulnotes.utils.ColorSelectUtil;
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
 * Date: 2015-10-18
 * Role: note display page of exact notebook
 */
public class NoteDisplayActivity extends BaseActivity implements View.OnClickListener, BaseRecyclerViewAdapter.onItemClickListener<Note> {

    private static final String TAG = "NoteDisplayActivity";

    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private RecyclerView noteRecyclerView;
    private FloatingActionButton fab;
    private String noteBookName;
    private FinalDb db;
    private int color;
    private int noteBookPosition;

    public static final int NOTE_BOOK_REQUEST_CODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NoteDisplayActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoteDisplayActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_display;
    }

    @Override
    protected void initViews() {
        Intent intent =getIntent();
        if (intent.hasExtra(NConstants.NOTE_BOOK_NAME)) {
            noteBookName = intent.getStringExtra(NConstants.NOTE_BOOK_NAME);
            getActionBarToolbar().setTitle(noteBookName);
            getActionBarToolbar().setTitleTextColor(context.getResources().getColor(R.color.black_transparency_54));
        }
        if (intent.hasExtra(NConstants.NOTE_BOOK_POSITION)) {
            noteBookPosition = intent.getIntExtra(NConstants.NOTE_BOOK_POSITION, -1);
            if (noteBookPosition >= 0) {
                color = ColorSelectUtil.selectColor(context, noteBookPosition);
                getActionBarToolbar().setBackgroundColor(color);
                fab = (FloatingActionButton) findViewById(R.id.note_display_floating_action_button);
                fab.setOnClickListener(this);
                fab.setBackgroundTintList(ColorStateList.valueOf(color));
            }
        }

    }

    @Override
    protected void initValues() {
        initNoteDisplayRecyclerView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe (threadMode = ThreadMode.MainThread)
    public void onUserEvent(NoteEvent event) {
        switch (event.getAction()) {
            case CREATE: {
                String noteBookName1 = event.getChangedNote().getNoteBookName();
                if (noteBookName.equals(noteBookName1)) {
                    noteList.add(0, event.getChangedNote());
                    noteAdapter.notifyItemInserted(0);
                }

                Toast.makeText(getApplicationContext(), getString(R.string.note_saved, noteBookName1), Toast.LENGTH_SHORT).show();
                break;
            }
            case EDIT: {
                noteList.set(event.getChangedNotePosition(), event.getChangedNote());
                noteAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), getString(R.string.note_saved, event.getChangedNote().getNoteBookName()), Toast.LENGTH_SHORT).show();
                break;
            }
            case EDIT_ADD_LOCATION: {
                noteList.set(event.getChangedNotePosition(), event.getChangedNote());
                noteAdapter.notifyDataSetChanged();

                break;
            }
            case CHANGE_NOTEBOOK: {
                int position = event.getChangedNotePosition();
                String noteBookName = event.getChangedNote().getNoteBookName();
                noteList.remove(position);
                noteAdapter.notifyItemRemoved(position);

                Toast.makeText(getApplicationContext(), getString(R.string.note_moved_to_other_notebook, noteBookName), Toast.LENGTH_SHORT).show();
                break;
            }
            case DELETE: {
                int position = event.getChangedNotePosition();
                noteList.remove(position);
                noteAdapter.notifyItemRemoved(position);

                Toast.makeText(getApplicationContext(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.note_display_floating_action_button: {
                startCreateNote();
            }
        }
    }

    @Override
    public void onClickListener(View view, int position, Note values) {
        switch (view.getId()) {
            case R.id.note_layout: {
                startViewOrEditNote(values, position, NoteActivity.NoteStatus.VIEW_NOTE);
                break;
            }
            case R.id.note_more: {
                showActionMoreMenu(view, position, values);
                break;
            }
        }

    }

    @Override
    public boolean onLongClickListener(View view, int position, Note values) {
        return false;
    }

    private void initNoteDisplayRecyclerView() {
        noteRecyclerView = (RecyclerView) findViewById(R.id.note_display_recycler_view);

        db = FinalDb.create(this, NConstants.NOTE_DB_NAME);
        noteList = initNotesData();
        noteAdapter = new NoteAdapter(noteList, this);
        noteAdapter.setOnItemClickListener(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        noteRecyclerView.setHasFixedSize(true);
        noteRecyclerView.setLayoutManager(layoutManager);
        noteRecyclerView.setAdapter(noteAdapter);
    }

    private List<Note> initNotesData() {
        List<Note> list;
        // notes ordered in update time decrease
        list = db.findAllByWhere(Note.class, "noteBookName='" + noteBookName + "'", "lastUpdateTimeInLong DESC");

        return list;
    }

    private void startCreateNote() {
        Intent intent = new Intent(NoteDisplayActivity.this, NoteActivity.class);
        intent.putExtra(NConstants.NOTE_TYPE, NoteActivity.NoteStatus.CREATE_NOTE);
        intent.putExtra(NConstants.NOTE_BOOK_NAME, noteBookName);
        intent.putExtra(NConstants.NOTE_BOOK_POSITION, noteBookPosition);
        startActivity(intent);

    }

    private void startViewOrEditNote(Note values, int notePosition, NoteActivity.NoteStatus status) {
        Intent intent = new Intent(NoteDisplayActivity.this, NoteActivity.class);
        intent.putExtra(NConstants.NOTE_TYPE, status);
        intent.putExtra(NConstants.COLOR, color);
        intent.putExtra(NConstants.NOTE_ID, values.getId());
        intent.putExtra(NConstants.NOTE_BOOK_POSITION, noteBookPosition);
        intent.putExtra(NConstants.NOTE_POSITION, notePosition);
        startActivity(intent);
    }

    private void showActionMoreMenu(View view, final int position, final Note values) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_note_display_more, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.menu_note_display_edit: {
                        startViewOrEditNote(values, position, NoteActivity.NoteStatus.EDIT_NOTE);
                        return true;
                    }
                    case R.id.menu_note_display_delete: {
                        showDeleteExactNoteDialog(position, values);
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

    private void showDeleteExactNoteDialog(final int position, final Note values) {
        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.note_book_delete_confirm)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.delete(values);

                        //update count of exact notebook
                        NoteBook noteBook = db.findAllByWhere(NoteBook.class, "name='" + noteBookName + "'").get(0);
                        int currentCount = db.findAllByWhere(Note.class, "noteBookName='" + noteBookName + "'").size();
                        noteBook.setCount(currentCount);
                        db.update(noteBook);

                        List<NoteBook> list = new ArrayList<>();
                        list.add(noteBook);
                        Map<NoteBook, Integer> map = new HashMap<>();
                        map.put(noteBook, noteBookPosition);

                        EventBus.getDefault().post(new NoteBookEvent(list, map, NoteBookEvent.NoteBookAction.UPDATE_NOTE_BOOK));

                        int posi = noteList.indexOf(values);
                        noteList.remove(posi);
                        noteAdapter.notifyItemRemoved(posi);

                        Toast.makeText(getApplicationContext(), R.string.note_deleted, Toast.LENGTH_SHORT).show();
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

}
