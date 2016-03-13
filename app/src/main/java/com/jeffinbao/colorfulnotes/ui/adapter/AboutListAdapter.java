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
 * Date: 2015-12-24
 */
public class AboutListAdapter extends BaseListAdapter<AboutListAdapter.ViewHolder> {

    public AboutListAdapter(Context context, List<String> list) {
        super(context, list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.about_list, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.aboutItemName.setText(itemNameList.get(position));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
       private AboutListAdapter adapter;
       public TextView aboutItemName;

       public ViewHolder(View view, AboutListAdapter adapter) {
           super(view);
           this.adapter = adapter;

           aboutItemName = (TextView) view.findViewById(R.id.about_item_name);
           aboutItemName.setOnClickListener(this);
       }

       @Override
       public void onClick(View v) {
           if (null != adapter.listener) {
               adapter.listener.onItemClick(v, getLayoutPosition(), adapter.getItem(getLayoutPosition()));
           }
       }
   }
}
