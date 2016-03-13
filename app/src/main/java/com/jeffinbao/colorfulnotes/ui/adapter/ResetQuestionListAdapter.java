package com.jeffinbao.colorfulnotes.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffinbao.colorfulnotes.R;

import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2016-02-08
 */
public class ResetQuestionListAdapter extends BaseListAdapter<ResetQuestionListAdapter.ViewHolder> {
    private String currentQuestion;

    public ResetQuestionListAdapter(Context context, List<String> questionNameList, String currentQuestion) {
        super(context, questionNameList);
        this.currentQuestion = currentQuestion;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_book_list, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.questionName.setText(itemNameList.get(position));
        if (itemNameList.get(position).equals(currentQuestion)) {
            holder.questionName.setTextColor(context.getResources().getColor(R.color.accent_material_light));
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView questionName;
        private ResetQuestionListAdapter adapter;

        public ViewHolder(View view, ResetQuestionListAdapter adapter) {
            super(view);

            questionName = (TextView) view.findViewById(R.id.list_note_book_name);
            this.adapter = adapter;

            questionName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != adapter.listener) {
                adapter.listener.onItemClick(v, getLayoutPosition(), adapter.getItem(getLayoutPosition()));
            }
        }
    }
}
