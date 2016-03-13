package com.jeffinbao.colorfulnotes.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.model.NoteBook;
import com.jeffinbao.colorfulnotes.utils.ColorSelectUtil;
import com.jeffinbao.colorfulnotes.utils.StringUtil;
import com.jeffinbao.colorfulnotes.utils.ViewUtil;

import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-10-15
 */
public class NoteBookAdapter extends BaseRecyclerViewAdapter<NoteBook, NoteBookAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
        public TextView noteBookName;
        public TextView noteBookCount;
        public CardView noteBookCardView;
        public ImageView noteBookMore;
        public NoteBookAdapter noteBookAdapter;

        public ViewHolder(View view, NoteBookAdapter adapter) {
            super(view);
            noteBookAdapter = adapter;
            noteBookName = (TextView) view.findViewById(R.id.note_book_title);
            noteBookCount = (TextView) view.findViewById(R.id.note_book_count);
            noteBookCardView = (CardView) view.findViewById(R.id.note_book_card_view);
            noteBookMore = (ImageView) view.findViewById(R.id.note_book_more);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            view.setOnTouchListener(this);
            noteBookMore.setOnClickListener(this);
            noteBookMore.setOnLongClickListener(this);
            noteBookMore.setOnTouchListener(this);
        }

        @Override
        public void onClick(View view) {
            if (null != noteBookAdapter.itemClickListener) {
                noteBookAdapter.itemClickListener.onClickListener(view, getLayoutPosition(), noteBookAdapter.list.get(getLayoutPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            return null != noteBookAdapter.itemClickListener && noteBookAdapter.itemClickListener.onLongClickListener(v, getLayoutPosition(), noteBookAdapter.list.get(getLayoutPosition()));
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return ViewUtil.viewTouchAlphaChange(v, event);
        }
    }

    private int lastPosition = -1;

    public NoteBookAdapter(List<NoteBook> list, Context context) {
        super(list, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_book_layout, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NoteBook noteBook = list.get(position);
        int titleChineseStringCount = StringUtil.getChineseStringCountWithThreshold(noteBook.getName(), NConstants.NOTEBOOK_TITLE_THRESHOLD);

        if (null ==  noteBook) {
            return;
        }

        if (StringUtil.getStringCharacterCount(noteBook.getName()) <= NConstants.NOTEBOOK_TITLE_THRESHOLD) {
            holder.noteBookName.setText(noteBook.getName());
        } else {
            holder.noteBookName.setText(noteBook.getName().substring(0, NConstants.NOTEBOOK_TITLE_THRESHOLD - titleChineseStringCount + 1) + NConstants.ELLIPSIS);
        }

        holder.noteBookCount.setText(String.valueOf(noteBook.getCount()));
        holder.noteBookCardView.setCardBackgroundColor(ColorSelectUtil.selectColor(context, position));

        playViewInScaleAnimatorSet(holder.itemView, 0.5f, 1.0f, position);

    }

    private void playViewInScaleAnimatorSet(View view, float From, float To, int position) {
        if (position > lastPosition) {

            ObjectAnimator animatorY = ObjectAnimator.ofFloat(view, "scaleY", From, To);

            AnimatorSet set = new AnimatorSet();
            set.play(animatorY);
            set.setDuration(500);
            set.start();

            lastPosition = position;
        }
    }

}
