package com.jeffinbao.colorfulnotes.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jeffinbao.colorfulnotes.R;

/**
 * Author: baojianfeng
 * Date: 2015-11-26
 */
public class PreferenceUtil {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static PreferenceUtil preferenceUtil;

    private PreferenceUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public synchronized static PreferenceUtil getDefault(Context context) {
        if (null == preferenceUtil) {
            preferenceUtil = new PreferenceUtil(context);
        }

        return preferenceUtil;
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putString(String key, String value) {
        editor.putString(key, value).commit();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).commit();
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, -1);
    }

    public void putInt(String key, int value) {
        editor.putInt(key, value).commit();
    }

}
