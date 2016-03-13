package com.jeffinbao.colorfulnotes.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-10-18
 */
public abstract class BaseRecyclerViewAdapter<E, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH>{

    protected List<E> list;
    protected Context context;
    protected onItemClickListener<E> itemClickListener;

    public BaseRecyclerViewAdapter(List<E> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(onItemClickListener<E> itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface onItemClickListener<E> {
        void onClickListener(View view, int position, E values);
        boolean onLongClickListener(View view, int position, E values);
    }

}
