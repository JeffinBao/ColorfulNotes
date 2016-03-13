package com.jeffinbao.colorfulnotes.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.utils.StringUtil;

import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-11-16
 */
public class NoteBookListAdapter extends BaseListAdapter<NoteBookListAdapter.ViewHolder> {
    private String currentNoteBook;

    public NoteBookListAdapter(Context context, List<String> list, String currentNoteBook) {
        super(context, list);
        this.currentNoteBook = currentNoteBook;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_book_list, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (itemNameList.get(position).equals(currentNoteBook)) {
            holder.noteBookName.setTextColor(context.getResources().getColor(R.color.material_deep_teal_500));
        }

        int titleChineseStringCount = StringUtil.getChineseStringCountWithThreshold(itemNameList.get(position), NConstants.NOTEBOOK_TITLE_THRESHOLD);

        if (StringUtil.getStringCharacterCount(itemNameList.get(position)) <= NConstants.NOTEBOOK_TITLE_THRESHOLD) {
            holder.noteBookName.setText(itemNameList.get(position));
        } else {
            holder.noteBookName.setText(itemNameList.get(position).substring(0, NConstants.NOTEBOOK_TITLE_THRESHOLD - titleChineseStringCount + 1) + NConstants.ELLIPSIS);
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView noteBookName;
        private NoteBookListAdapter adapter;

        public ViewHolder(View view, NoteBookListAdapter adapter) {
            super(view);
            noteBookName = (TextView) view.findViewById(R.id.list_note_book_name);
            this.adapter = adapter;

            noteBookName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != adapter.listener) {
                adapter.listener.onItemClick(v, getLayoutPosition(), adapter.getItem(getLayoutPosition()));
            }
        }
    }

}
