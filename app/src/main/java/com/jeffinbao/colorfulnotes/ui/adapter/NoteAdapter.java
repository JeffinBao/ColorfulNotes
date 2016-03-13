package com.jeffinbao.colorfulnotes.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.model.Note;
import com.jeffinbao.colorfulnotes.utils.StringUtil;
import com.jeffinbao.colorfulnotes.utils.ViewUtil;

import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-10-20
 */
public class NoteAdapter extends BaseRecyclerViewAdapter<Note, NoteAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        private TextView noteTitle;
        private TextView noteContentHint;
        private TextView noteLastUpdateTime;
        private TextView noteCreateLocation;
        private ImageView noteMore;
        private NoteAdapter noteAdapter;

        public ViewHolder(View view, NoteAdapter adapter) {
            super(view);
            noteAdapter = adapter;
            noteTitle = (TextView) view.findViewById(R.id.note_title);
            noteContentHint = (TextView) view.findViewById(R.id.note_content_hint);
            noteLastUpdateTime = (TextView) view.findViewById(R.id.note_last_update_time);
            noteCreateLocation = (TextView) view.findViewById(R.id.note_create_location);
            noteMore = (ImageView) view.findViewById(R.id.note_more);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnTouchListener(this);
            noteMore.setOnClickListener(this);
            noteMore.setOnLongClickListener(this);
            noteMore.setOnTouchListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != noteAdapter.itemClickListener) {
                noteAdapter.itemClickListener.onClickListener(v, getLayoutPosition(), noteAdapter.list.get(getLayoutPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return null != noteAdapter.itemClickListener && noteAdapter.itemClickListener.onLongClickListener(v, getLayoutPosition(), noteAdapter.list.get(getLayoutPosition()));
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return ViewUtil.viewTouchAlphaChange(v, event);
        }
    }

    public NoteAdapter(List<Note> list, Context context) {
        super(list, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = list.get(position);
        int titleChineseStringCount = StringUtil.getChineseStringCountWithThreshold(note.getTitle(), NConstants.NOTE_TITLE_THRESHOLD);
        int contentChineseStringCount = StringUtil.getChineseStringCountWithThreshold(note.getContent(), NConstants.NOTE_CONTENT_THRESHOLD);

        if (null == note) {
            return;
        }

        if (StringUtil.getStringCharacterCount(note.getTitle()) <= NConstants.NOTE_TITLE_THRESHOLD) {
            holder.noteTitle.setText(note.getTitle());
        } else {
            holder.noteTitle.setText(note.getTitle().substring(0, NConstants.NOTE_TITLE_THRESHOLD - titleChineseStringCount + 1) + NConstants.ELLIPSIS);
        }

        if (StringUtil.getStringCharacterCount(note.getContent()) <= NConstants.NOTE_CONTENT_THRESHOLD) {
            holder.noteContentHint.setText(note.getContent());
        } else {
            holder.noteContentHint.setText(note.getContent().substring(0, NConstants.NOTE_CONTENT_THRESHOLD - contentChineseStringCount + 1) + NConstants.ELLIPSIS);
        }

        holder.noteLastUpdateTime.setText(context.getString(R.string.note_last_update_time, note.getLastUpdateTime()));
        if (null != note.getCreateLocation()) {
            holder.noteCreateLocation.setVisibility(View.VISIBLE);
            holder.noteCreateLocation.setText(context.getString(R.string.note_create_location, note.getCreateLocation()));
        }
    }

}
