package com.jeffinbao.colorfulnotes.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.ui.view.PasscodeView;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;

/**
 * Author: baojianfeng
 * Date: 2016-01-24
 */
public class PasscodeActivity extends BaseActivity implements PasscodeView.PasscodeActionStatusListener {
    private static final int PASSCODE_INIT_SUCCESS = 0x001;

    private PasscodeView passcodeView;
    private String passcodeAction;
    private PreferenceUtil preferenceUtil;
    private PasscodeHandler handler;

    private static class PasscodeHandler extends Handler {
        private WeakReference<PasscodeActivity> weakReference;

        public PasscodeHandler(PasscodeActivity activity) {
            weakReference = new WeakReference<PasscodeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            PasscodeActivity activity = weakReference.get();
            if (null == activity) {
                return;
            }

            switch (msg.what) {
                case PASSCODE_INIT_SUCCESS: {
                    if (!activity.preferenceUtil.getBoolean(activity.getString(R.string.passcode_reset_has_shown_for_once))) {
                        activity.preferenceUtil.putBoolean(activity.getString(R.string.passcode_reset_has_shown_for_once), true);
                        Intent intent = new Intent(activity, PasscodeResetActivity.class);
                        intent.putExtra(NConstants.PASSCODE_RESET_QUESTION_TYPE, NConstants.INIT_RESET_QUESTION);
                        activity.startActivity(intent);
                        activity.finish();
                    } else {
                        Toast.makeText(activity.getApplicationContext(), R.string.passcode_reset_successfully, Toast.LENGTH_SHORT).show();
                        activity.finish();
                    }
                }
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_passcode;
    }

    @Override
    protected void initViews() {
        passcodeAction = getIntent().getStringExtra(NConstants.PASSCODE_ACTION_TYPE);

        passcodeView = (PasscodeView) findViewById(R.id.passcode_view_in_passcode_activity);
        passcodeView.setPasscodeActionType(passcodeAction);
        passcodeView.setPasscodeActionStatusListener(this);

    }

    @Override
    protected void initValues() {
        preferenceUtil = PreferenceUtil.getDefault(this);
        handler = new PasscodeHandler(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("PasscodeActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PasscodeActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onInitSuccess() {
        handler.sendEmptyMessage(PASSCODE_INIT_SUCCESS);
    }

    @Override
    public void onValidateSuccess() {

    }

    @Override
    public void onChangeSuccess() {
        Toast.makeText(getApplicationContext(), getString(R.string.passcode_change_success), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onPasscodeForget() {

    }
}
