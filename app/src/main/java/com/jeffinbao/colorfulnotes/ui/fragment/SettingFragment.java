package com.jeffinbao.colorfulnotes.ui.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.constants.NConstants;
import com.jeffinbao.colorfulnotes.ui.PasscodeActivity;
import com.jeffinbao.colorfulnotes.ui.PasscodeResetActivity;
import com.jeffinbao.colorfulnotes.utils.PreferenceUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * Author: baojianfeng
 * Date: 2016-01-31
 */
public class SettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private View view;
    private SwitchPreference passcodeSwitchPref;
    private Preference passcodeSettingPref;
    private Preference passcodeResetPref;
    private PreferenceCategory settingPrefCategory;
    private Resources resources;
    private PreferenceUtil preferenceUtil;

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SettingActivity");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SettingActivity");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = super.onCreateView(inflater, container, savedInstanceState);
        init();

        return view;
    }

    private void init() {
        Intent intent = getActivity().getIntent();
        resources = getResources();
        preferenceUtil = PreferenceUtil.getDefault(getActivity());
        addPreferencesFromResource(R.xml.preference_setting);

        passcodeSwitchPref = (SwitchPreference) findPreference(resources.getString(R.string.preference_passcode_open_or_not));
        passcodeSettingPref = findPreference(resources.getString(R.string.preference_passcode_change));
        passcodeResetPref = findPreference(resources.getString(R.string.preference_passcode_find));
        settingPrefCategory = (PreferenceCategory) findPreference(resources.getString(R.string.preference_passcode_setting_key));

        passcodeSwitchPref.setOnPreferenceClickListener(this);
        passcodeSettingPref.setOnPreferenceClickListener(this);
        passcodeResetPref.setOnPreferenceClickListener(this);

        if (intent.getBooleanExtra(NConstants.PASSCODE_FORGET, false)) {
            settingPrefCategory.removePreference(passcodeSwitchPref);
        }

        if (preferenceUtil.getString(resources.getString(R.string.passcode_availability_preference)).equals("")) {
            settingPrefCategory.removePreference(passcodeSwitchPref);
            passcodeSettingPref.setTitle(resources.getString(R.string.preference_passcode_init_title));
        }
        if (preferenceUtil.getString(resources.getString(R.string.passcode_reset_question_preference)).equals("")
                || preferenceUtil.getString(resources.getString(R.string.passcode_reset_answer_preference)).equals("")) {
            settingPrefCategory.removePreference(passcodeResetPref);
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if (preference.equals(passcodeSettingPref)) {
            String title = passcodeSettingPref.getTitle().toString();
            if (title.equals(getString(R.string.preference_passcode_init_title))) {
                Intent intent = new Intent(getActivity(), PasscodeActivity.class);
                intent.putExtra(NConstants.PASSCODE_ACTION_TYPE, NConstants.INIT_PASSCODE);
                startActivity(intent);
                getActivity().finish();
            } else if (title.equals(getString(R.string.preference_passcode_change_title))) {
                Intent intent = new Intent(getActivity(), PasscodeActivity.class);
                intent.putExtra(NConstants.PASSCODE_ACTION_TYPE, NConstants.CHANGE_PASSCODE);
                startActivity(intent);
            }
        } else if (preference.equals(passcodeResetPref)) {
            Intent intent = new Intent(getActivity(), PasscodeResetActivity.class);
            intent.putExtra(NConstants.PASSCODE_RESET_QUESTION_TYPE, NConstants.VALIDATE_RESET_QUESTION);
            startActivity(intent);
        } else if (preference.equals(passcodeSwitchPref)) {
            preferenceUtil.putBoolean(getString(R.string.preference_passcode_open_or_not), passcodeSwitchPref.isChecked());
        }
        return false;
    }
}
