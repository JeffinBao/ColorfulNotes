package com.jeffinbao.colorfulnotes.ui.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.utils.CryptoUtil;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;

import java.lang.ref.WeakReference;

/**
 * Author: baojianfeng
 * Date: 2016-01-31
 */
public class PasscodeView extends RelativeLayout implements View.OnClickListener {
    private static final int PASSCODE_INDICATION_RESUME = 0x001;

    private Context context;
    private int passscodeCursor = 0;
    private String[] passcodes = new String[4];
    private String passcodeString;
    private ImageView[] passcodeStars = new ImageView[4];
    private TextView[] passcodeDigits = new TextView[10];
    private TextView passcodeIndication;
    private TextView passcodeForget;
    private ImageView passcodeClear;
    private String action;
    private int step = 1;
    private PasscodeViewHandler handler;
    private PreferenceUtil preferenceUtil;
    private PasscodeActionStatusListener actionStatusListener;

    private static class PasscodeViewHandler extends Handler {
        private WeakReference<PasscodeView> viewWeakReference;
        private Context context;

        public PasscodeViewHandler(PasscodeView view, Context context) {
            viewWeakReference = new WeakReference<PasscodeView>(view);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            PasscodeView view = viewWeakReference.get();

            if (null == view) {
                return;
            }

            switch (msg.what) {
                case PASSCODE_INDICATION_RESUME: {
                    switch (view.action) {
                        case NConstants.INIT_PASSCODE: {
                            view.passcodeIndication.setText(R.string.passcode_init_enter);
                            view.passcodeIndication.setTextColor(context.getResources().getColor(R.color.black_transparency_54));
                            break;
                        }
                        case NConstants.VALIDATE_PASSCODE: {
                            view.passcodeIndication.setText(R.string.passcode_validation);
                            view.passcodeIndication.setTextColor(context.getResources().getColor(R.color.black_transparency_54));
                            break;
                        }
                        case NConstants.CHANGE_PASSCODE: {
                            if (view.step == 1) {
                                view.passcodeIndication.setText(R.string.passcode_change_enter_old);
                                view.passcodeIndication.setTextColor(context.getResources().getColor(R.color.black_transparency_54));
                            } else {
                                view.passcodeIndication.setText(R.string.passcode_change_enter_new);
                                view.passcodeIndication.setTextColor(context.getResources().getColor(R.color.black_transparency_54));
                            }
                            break;
                        }
                    }
                    break;
                }
            }
        }


    }

    public PasscodeView(Context context) {
        super(context);
        init(context);
        this.context = context;
    }

    public PasscodeView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context);
        this.context = context;
    }

    public PasscodeView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        init(context);
        this.context = context;
    }

    public void setPasscodeActionType(String action) {
        this.action = action;

        switch (action) {
            case NConstants.INIT_PASSCODE: {
                passcodeIndication.setText(R.string.passcode_init_enter);
                break;
            }
            case NConstants.VALIDATE_PASSCODE: {
                passcodeForget = (TextView) findViewById(R.id.passcode_forget);
                passcodeForget.setVisibility(VISIBLE);
                passcodeForget.setOnClickListener(this);
                passcodeIndication.setText(R.string.passcode_validation);
                break;
            }
            case NConstants.CHANGE_PASSCODE: {
                passcodeIndication.setText(R.string.passcode_change_enter_old);
                break;
            }
        }
    }

    public void setPasscodeActionStatusListener(PasscodeActionStatusListener actionStatus) {
        this.actionStatusListener = actionStatus;
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.passcode_view_layout, this, true);
        initViews();

        handler = new PasscodeViewHandler(this, context);
        preferenceUtil = PreferenceUtil.getDefault(context);
    }

    private void initViews() {
        passcodeDigits[0] = (TextView) findViewById(R.id.passcode_digit_0);
        passcodeDigits[1] = (TextView) findViewById(R.id.passcode_digit_1);
        passcodeDigits[2] = (TextView) findViewById(R.id.passcode_digit_2);
        passcodeDigits[3] = (TextView) findViewById(R.id.passcode_digit_3);
        passcodeDigits[4] = (TextView) findViewById(R.id.passcode_digit_4);
        passcodeDigits[5] = (TextView) findViewById(R.id.passcode_digit_5);
        passcodeDigits[6] = (TextView) findViewById(R.id.passcode_digit_6);
        passcodeDigits[7] = (TextView) findViewById(R.id.passcode_digit_7);
        passcodeDigits[8] = (TextView) findViewById(R.id.passcode_digit_8);
        passcodeDigits[9] = (TextView) findViewById(R.id.passcode_digit_9);
        for (TextView passcodeDigit : passcodeDigits) {
            passcodeDigit.setOnClickListener(this);
        }

        passcodeClear = (ImageView) findViewById(R.id.passcode_clear_action);
        passcodeClear.setOnClickListener(this);

        passcodeIndication = (TextView) findViewById(R.id.passcode_text_indication);

        passcodeStars[0] = (ImageView) findViewById(R.id.passcode_star_1);
        passcodeStars[1] = (ImageView) findViewById(R.id.passcode_star_2);
        passcodeStars[2] = (ImageView) findViewById(R.id.passcode_star_3);
        passcodeStars[3] = (ImageView) findViewById(R.id.passcode_star_4);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.passcode_digit_0: {
                performDigitInputAction(0);
                break;
            }
            case R.id.passcode_digit_1: {
                performDigitInputAction(1);
                break;
            }
            case R.id.passcode_digit_2: {
                performDigitInputAction(2);
                break;
            }
            case R.id.passcode_digit_3: {
                performDigitInputAction(3);
                break;
            }
            case R.id.passcode_digit_4: {
                performDigitInputAction(4);
                break;
            }
            case R.id.passcode_digit_5: {
                performDigitInputAction(5);
                break;
            }
            case R.id.passcode_digit_6: {
                performDigitInputAction(6);
                break;
            }
            case R.id.passcode_digit_7: {
                performDigitInputAction(7);
                break;
            }
            case R.id.passcode_digit_8: {
                performDigitInputAction(8);
                break;
            }
            case R.id.passcode_digit_9: {
                performDigitInputAction(9);
                break;
            }
            case R.id.passcode_clear_action: {
                performClearLastInput();
                break;
            }
            case R.id.passcode_forget: {
                actionStatusListener.onPasscodeForget();
                break;
            }
        }
    }

    private void performDigitInputAction(int value) {
        switch (passscodeCursor) {
            case 0: {
                passcodes[0] = String.valueOf(value);
                passcodeStars[0].setImageResource(R.mipmap.passcode_star_selected);
                break;
            }
            case 1: {
                passcodes[1] = String.valueOf(value);
                passcodeStars[1].setImageResource(R.mipmap.passcode_star_selected);
                break;
            }
            case 2: {
                passcodes[2] = String.valueOf(value);
                passcodeStars[2].setImageResource(R.mipmap.passcode_star_selected);
                break;
            }
            case 3: {
                passcodes[3] = String.valueOf(value);
                passcodeStars[3].setImageResource(R.mipmap.passcode_star_selected);
                break;
            }

        }

        if (passscodeCursor == 3) {
            processPasscode(action);
        } else {
            passscodeCursor++;
        }
    }

    private void processPasscode(String action) {
        if (null == action) {
            return;
        }

        switch (action) {
            case NConstants.INIT_PASSCODE: {
                processInitAction();
                break;
            }
            case NConstants.VALIDATE_PASSCODE: {
                processValidateAction();
                break;
            }
            case NConstants.CHANGE_PASSCODE: {
                processChangeAction();
                break;
            }
        }
    }

    private void processInitAction() {
        if (step == 1) {
            passcodeString = passcodes[0] + passcodes[1] + passcodes[2] + passcodes[3];
            passcodeIndication.setText(R.string.passcode_init_enter_again);
            step++;
            reset();
        } else {
            String passcode = passcodes[0] + passcodes[1] + passcodes[2] + passcodes[3];

            if (passcode.equals(passcodeString)) {
                toggleButtons(false);
                String passcodeHash = generatePasscodeHash(passcode);
                preferenceUtil.putString(context.getString(R.string.passcode_availability_preference), passcodeHash);
                if (null != actionStatusListener) {
                    actionStatusListener.onInitSuccess();
                }
            } else {
                passcodeIndication.setText(R.string.passcode_not_match_as_last_input);
                passcodeIndication.setTextColor(context.getResources().getColor(R.color.red_u1));
                step = 1;
                handler.sendEmptyMessageDelayed(PASSCODE_INDICATION_RESUME, 2000);
                reset();
            }
        }
    }

    private void processValidateAction() {
        String passcode = passcodes[0] + passcodes[1] + passcodes[2] + passcodes[3];
        String passcodeHash = generatePasscodeHash(passcode);

        if (isPasscodeValid(passcodeHash)) {
            toggleButtons(false);
            if (null != actionStatusListener) {
                actionStatusListener.onValidateSuccess();
            }
        } else {
            passcodeIndication.setText(R.string.passcode_validation_wrong);
            passcodeIndication.setTextColor(context.getResources().getColor(R.color.red_u1));
            handler.sendEmptyMessageDelayed(PASSCODE_INDICATION_RESUME, 2000);
            reset();
        }

    }

    private void processChangeAction() {
        if (step == 1) {
            String passcode = passcodes[0] + passcodes[1] + passcodes[2] + passcodes[3];
            String passcodeHash = generatePasscodeHash(passcode);

            if (isPasscodeValid(passcodeHash)) {
                passcodeIndication.setText(R.string.passcode_change_enter_new);
                step++;
            } else {
                passcodeIndication.setText(R.string.passcode_change_enter_old_wrong);
                passcodeIndication.setTextColor(context.getResources().getColor(R.color.red_u1));
                handler.sendEmptyMessageDelayed(PASSCODE_INDICATION_RESUME, 2000);
            }
            reset();
        } else if (step == 2) {
            passcodeIndication.setText(R.string.passcode_change_enter_new_again);
            passcodeString = passcodes[0] + passcodes[1] + passcodes[2] + passcodes[3];
            step++;
            reset();
        } else {
            String passcode = passcodes[0] + passcodes[1] + passcodes[2] + passcodes[3];

            if (passcode.equals(passcodeString)) {
                toggleButtons(false);
                String passcodeHash = generatePasscodeHash(passcode);
                preferenceUtil.putString(context.getString(R.string.passcode_availability_preference), passcodeHash);
                if (null != actionStatusListener) {
                    actionStatusListener.onChangeSuccess();
                }
            } else {
                passcodeIndication.setText(R.string.passcode_not_match_as_last_input);
                passcodeIndication.setTextColor(context.getResources().getColor(R.color.red_u1));
                step = 2;
                handler.sendEmptyMessageDelayed(PASSCODE_INDICATION_RESUME, 2000);
                reset();
            }

        }
    }

    private String generatePasscodeHash(String passcode) {
        return CryptoUtil.md5(passcode);
    }

    //TODO 为什么getString会得到多余的4个空格？
    private boolean isPasscodeValid(String passcodeHash) {
        String exactPasscodeHash = preferenceUtil.getString(context.getString(R.string.passcode_availability_preference)).trim();
        return exactPasscodeHash.equals(passcodeHash.trim());
    }

    private void performClearLastInput() {
        if (passscodeCursor == 0) {
            return;
        }

        passscodeCursor--;
        passcodes[passscodeCursor] = "";
        passcodeStars[passscodeCursor].setImageResource(R.mipmap.passcode_star_unselected);

    }

    private void toggleButtons(boolean flag) {
        for (TextView passcodeDigit : passcodeDigits) {
            passcodeDigit.setEnabled(flag);
        }

        passcodeClear.setEnabled(flag);
        if (null != passcodeForget) {
            passcodeForget.setEnabled(flag);
        }
    }

    private void reset() {
        passscodeCursor = 0;

        for (int i = 0; i < 4; i++) {
            passcodes[i] = "";
            passcodeStars[i].setImageResource(R.mipmap.passcode_star_unselected);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reset();
    }

    public interface PasscodeActionStatusListener {
        void onInitSuccess();

        void onValidateSuccess();

        void onChangeSuccess();

        void onPasscodeForget();
    }
}
