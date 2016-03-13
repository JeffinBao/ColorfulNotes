package com.jeffinbao.colorfulnotes.ui;

import android.view.View;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.ui.fragment.SettingFragment;
import com.umeng.analytics.MobclickAgent;

/**
 * Author: baojianfeng
 * Date: 2016-01-31
 */
public class SettingActivity extends BaseActivity {

    private SettingFragment settingFragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initViews() {
        getActionBarToolbar().setTitle(R.string.setting);

        settingFragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(R.id.setting_container, settingFragment).commit();

    }

    @Override
    protected void initValues() {
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
        MobclickAgent.onPageStart("SettingActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
        MobclickAgent.onPause(this);
    }
}
