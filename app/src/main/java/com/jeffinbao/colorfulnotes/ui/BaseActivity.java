package com.jeffinbao.colorfulnotes.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jeffinbao.colorfulnotes.R;

import de.greenrobot.event.Subscribe;

/**
 * Author: baojianfeng
 * Date: 2015-10-08
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Toolbar actionBarToolbar;
    protected Context context;

    @Override
    @Subscribe
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        context = this;
        initToolbar();

        initViews();
        initValues();
    }

    protected void initToolbar() {
        actionBarToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != actionBarToolbar) {
            actionBarToolbar.setBackgroundColor(getResources().getColor(R.color.white));
            actionBarToolbar.setTitle(R.string.app_name);
            actionBarToolbar.setTitleTextColor(getResources().getColor(R.color.black_transparency_54));
            actionBarToolbar.collapseActionView();

            setSupportActionBar(actionBarToolbar);
            if (null != getSupportActionBar()) {
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_material);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

        }
    }

    protected Toolbar getActionBarToolbar() {
        return actionBarToolbar;
    }

    protected abstract int getLayoutId();

    protected abstract void initViews();

    protected abstract void initValues();
}
