package com.jeffinbao.colorfulnotes.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.jeffinbao.colorfulnotes.CApplication;
import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.ui.adapter.AboutListAdapter;
import com.jeffinbao.colorfulnotes.ui.adapter.BaseListAdapter;
import com.jeffinbao.colorfulnotes.utils.OSUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.Arrays;
import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2015-12-23
 */
public class AboutActivity extends BaseActivity implements BaseListAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private AboutListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AboutActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AboutActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.about_recycler_view);
    }

    @Override
    protected void initValues() {
        getActionBarToolbar().setTitle(R.string.about);

        List<String> aboutItemList = Arrays.asList(getResources().getStringArray(R.array.about_item_list));
        adapter = new AboutListAdapter(this, aboutItemList);
        adapter.setItemClickListener(this);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void onItemClick(View view, int position, String name) {
        switch (position) {
            case 0: {
                likeOurApps();
                break;
            }
            case 1: {
                encouragementForAuthor();
                break;
            }
            case 2: {
                contactAuthor();
                break;
            }
            case 3: {
                privatePolicy();
                break;
            }
        }
    }

    /**
     * thumbs up app in app stores
     */
    private void likeOurApps() {
        try {
            Uri uri = Uri.parse("market://details?id=" + getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * donate to developer
     */
    private void encouragementForAuthor() {
        new AlertDialog.Builder(this)
                .setView(R.layout.encouragement_for_author_dialog_layout)
                .setCancelable(true)
                .create()
                .show();
    }

    /**
     * invoke default mailbox to write email to developer
     */
    private void contactAuthor() {
        try {
            Uri uri = Uri.parse("mailto:bjf13609846991@gmail.com");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            PackageManager pm = getPackageManager();
            List<ResolveInfo> infos = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            if (null == infos || infos.size() <= 0) {
                Toast.makeText(this, R.string.install_mail_app_first, Toast.LENGTH_SHORT).show();
            } else {
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * open private policy page
     */
    private void privatePolicy() {
        if (!OSUtil.isNetworkAvailable()) {
            Toast.makeText(CApplication.getAppContext(), R.string.open_network, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(AboutActivity.this, PrivatePolicyActivity.class);
        startActivity(intent);
    }
}
