package com.jeffinbao.colorfulnotes.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.ui.fragment.ChoosePasscodeResetQuestionDialogFragment;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;
import com.jeffinbao.colorfulnotes.utils.SoftKeyboardUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Author: baojianfeng
 * Date: 2016-02-06
 */
public class PasscodeResetActivity extends BaseActivity implements View.OnClickListener, ChoosePasscodeResetQuestionDialogFragment.ResetQuestionChangedListener {

    private TextView questionTextView;
    private ImageView questionMoreImageView;
    private EditText answerEditText;
    private MenuItem menuItemDone;
    private PreferenceUtil preferenceUtil;
    private String resetQuestionActionType;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_passcode_reset;
    }

    @Override
    protected void initViews() {
        SoftKeyboardUtil.showKeyboard(this);
        getActionBarToolbar().setTitle(R.string.passcode_reset_title);
        questionTextView = (TextView) findViewById(R.id.passcode_reset_question);
        questionMoreImageView = (ImageView) findViewById(R.id.passcode_reset_question_more);
        answerEditText = (EditText) findViewById(R.id.passcode_reset_answer_edit_text);
    }

    @Override
    protected void initValues() {
        preferenceUtil = PreferenceUtil.getDefault(this);
        answerEditText.addTextChangedListener(new PasscodeRestTextWatcher());

        resetQuestionActionType = getIntent().getStringExtra(NConstants.PASSCODE_RESET_QUESTION_TYPE);
        if (resetQuestionActionType.equals(NConstants.INIT_RESET_QUESTION)) {
            questionMoreImageView.setVisibility(View.VISIBLE);
            questionMoreImageView.setOnClickListener(this);
        } else {
            String resetQuestion = preferenceUtil.getString(getString(R.string.passcode_reset_question_preference));
            questionTextView.setText(resetQuestion);
        }

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
        MobclickAgent.onPageStart("PasscodeResetActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("PasscodeResetActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        if (resetQuestionActionType.equals(NConstants.INIT_RESET_QUESTION)) {
            showResetQuestionNotSetDialog();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_passcode_reset, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menuItemDone = menu.getItem(0);
        menuItemDone.setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.passcode_reset_answer_done: {
                if (resetQuestionActionType.equals(NConstants.INIT_RESET_QUESTION)) {
                    SoftKeyboardUtil.hideKeyboard(this);

                    preferenceUtil.putString(getString(R.string.passcode_reset_question_preference), questionTextView.getText().toString());
                    preferenceUtil.putString(getString(R.string.passcode_reset_answer_preference), answerEditText.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), R.string.passcode_reset_question_set_successfully, Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String resetAnswer = preferenceUtil.getString(getString(R.string.passcode_reset_answer_preference));
                    if (answerEditText.getText().toString().trim().equals(resetAnswer)) {
                        SoftKeyboardUtil.hideKeyboard(this);

                        Intent intent = new Intent(this, PasscodeActivity.class);
                        intent.putExtra(NConstants.PASSCODE_ACTION_TYPE, NConstants.INIT_PASSCODE);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.passcode_reset_question_validation_fail, Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.passcode_reset_question_more) {
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.reset_question_list));
            ArrayList<String> questionList = new ArrayList<>(list);
            String currentQuestion = questionTextView.getText().toString();
            ChoosePasscodeResetQuestionDialogFragment fragment = ChoosePasscodeResetQuestionDialogFragment.getInstance(questionList, currentQuestion);
            fragment.setResetQuestionChangedListener(this);
            fragment.show(getFragmentManager(), "choose_reset_question");
        }
    }

    @Override
    public void onResetQuestionChanged(String name, int position) {
        questionTextView.setText(name);
    }

    private void showResetQuestionNotSetDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage(R.string.passcode_reset_question_not_set)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        AlertDialog alertDialog = dialog.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlideInAndOutAnimation;
        alertDialog.show();
    }

    class PasscodeRestTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (null == menuItemDone) {
                return;
            }

            if (answerEditText.getText().length() == 0) {
                menuItemDone.setVisible(false);
            } else {
                menuItemDone.setVisible(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }
}
