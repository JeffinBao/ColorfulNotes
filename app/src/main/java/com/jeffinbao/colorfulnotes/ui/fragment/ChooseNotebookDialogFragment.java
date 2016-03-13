package com.jeffinbao.colorfulnotes.ui.fragment;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.event.NoteBookEvent;
import com.jeffinbao.colorfulnotes.model.NoteBook;
import com.jeffinbao.colorfulnotes.ui.adapter.BaseListAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.NoteBookListAdapter;
import com.jeffinbao.colorfulnotes.utils.SoftKeyboardUtil;
import com.umeng.analytics.MobclickAgent;

import net.tsz.afinal.FinalDb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * Author: baojianfeng
 * Date: 2015-11-16
 */
public class ChooseNotebookDialogFragment extends BaseDialogFragment implements BaseListAdapter.OnItemClickListener, View.OnClickListener {
    private static final String NOTE_BOOK_LIST = "note_book_list";
    private static final String CURRENT_NOTE_BOOK = "current_note_book";
    private NoteBookChangeListener listener;
    private ArrayList<String> noteBookNamesList;
    private NoteBookListAdapter adapter;

    public static ChooseNotebookDialogFragment getInstance(ArrayList<String> noteBookNamesList, String currentNoteBookName) {

        ChooseNotebookDialogFragment fragment = new ChooseNotebookDialogFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(NOTE_BOOK_LIST, noteBookNamesList);
        args.putString(CURRENT_NOTE_BOOK, currentNoteBookName);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.NoteDialogFragmentStyle);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NoteActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoteActivity");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_note_book, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.fragment_create_new_note_book).setOnClickListener(this);

        noteBookNamesList = getArguments().getStringArrayList(NOTE_BOOK_LIST);
        String currentNoteBookName = getArguments().getString(CURRENT_NOTE_BOOK);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.fragment_choose_note_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        adapter = new NoteBookListAdapter(getActivity(), noteBookNamesList, currentNoteBookName);
        adapter.setItemClickListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels * 50 / 100;
        recyclerView.getLayoutParams().height = height;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_create_new_note_book: {
                showAddNewNoteBookDialog();
                break;
            }
        }
    }

    @Override
    public void onItemClick(View view, int position, String name) {
//        TextView textView = (TextView) getActivity().findViewById(R.id.exact_note_book);
//        textView.setText(name);

        listener.onNoteBookChange(name, position);

        dismiss();
    }

    private void showAddNewNoteBookDialog() {
        SoftKeyboardUtil.showKeyboard(getActivity());

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.note_book_new_or_edit_dialog, null);
        dialog.setView(layout);
        dialog.setMessage(R.string.note_book_new);
        final EditText input = (EditText) layout.findViewById(R.id.note_book_edittext_title);

        dialog.setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SoftKeyboardUtil.hideKeyboard(getActivity().getApplicationContext(), input);

                if (input.getText().length() > 0 && !isNoteBookNameExist(input.getText().toString())) {
                    FinalDb db = FinalDb.create(getActivity(), NConstants.NOTE_DB_NAME);

                    NoteBook noteBook = new NoteBook();
                    noteBook.setCount(0);
                    noteBook.setName(input.getText().toString());
                    db.saveBindId(noteBook);

                    noteBookNamesList.add(input.getText().toString());
                    adapter.notifyItemInserted(noteBookNamesList.size() - 1);

                    List<NoteBook> list = new ArrayList<>();
                    list.add(noteBook);
                    Map<NoteBook, Integer> map = new HashMap<>();
                    map.put(noteBook, noteBookNamesList.size() - 1);
                    EventBus.getDefault().post(new NoteBookEvent(list, map, NoteBookEvent.NoteBookAction.ADD_NEW_NOTE_BOOK));

                } else if (input.getText().length() == 0) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.note_book_name_should_not_be_null, Toast.LENGTH_SHORT).show();

                } else if (isNoteBookNameExist(input.getText().toString())) {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.note_book_name_should_note_be_same, Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SoftKeyboardUtil.hideKeyboard(getActivity().getApplicationContext(), input);
            }
        });

        dialog.show();
    }

    private boolean isNoteBookNameExist(String name) {
        for (int i = 0; i < noteBookNamesList.size(); i++) {
            if (name.equals(noteBookNamesList.get(i))) {
                return true;
            }
        }

        return false;
    }

    public void setNoteBookChangeListener(NoteBookChangeListener listener) {
        this.listener = listener;
    }

    public interface NoteBookChangeListener {
        void onNoteBookChange(String name, int position);
    }
}
