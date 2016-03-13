package com.jeffinbao.colorfulnotes.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-11-20
 */
public abstract class BaseListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected List<String> itemNameList;
    protected Context context;
    protected OnItemClickListener listener;

    public BaseListAdapter(Context context, List<String> itemNameList) {
        this.context = context;
        this.itemNameList = itemNameList;
    }

    @Override
    public int getItemCount() {
        return itemNameList.size();
    }

    protected String getItem(int position) {
        return itemNameList.get(position);
    }

    public void setItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener {
        void onItemClick(View view, int position, String name);
    }
}
