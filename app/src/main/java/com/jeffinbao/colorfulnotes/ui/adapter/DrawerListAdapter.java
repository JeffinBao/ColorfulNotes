package com.jeffinbao.colorfulnotes.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;

import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-11-20
 */
public class DrawerListAdapter extends BaseListAdapter<DrawerListAdapter.ViewHolder> {
    private String evernoteUserName;

    public DrawerListAdapter(Context context, List<String> list) {
        super(context, list);
        evernoteUserName = PreferenceUtil.getDefault(context).getString(context.getString(R.string.evernote_user_name));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_list, parent, false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.drawerItemName.setText(itemNameList.get(position));

        if (position == 0 && !TextUtils.isEmpty(evernoteUserName)) {
            holder.drawerItemName.setText(context.getString(R.string.evernote_unbind_hint, evernoteUserName));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView drawerItemName;
        private DrawerListAdapter adapter;

        public ViewHolder(View view, DrawerListAdapter adapter) {
            super(view);
            this.adapter = adapter;

            drawerItemName = (TextView) view.findViewById(R.id.list_drawer_item_name);
            drawerItemName.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (null != adapter.listener) {
                adapter.listener.onItemClick(v, getLayoutPosition(), adapter.getItem(getLayoutPosition()));
            }
        }
    }
}
