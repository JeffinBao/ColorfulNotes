package com.jeffinbao.colorfulnotes.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import com.jeffinbao.colorfulnotes.CApplication;

/**
 * Author: baojianfeng
 * Date: 2015-11-19
 */
public class OSUtil {

    /**
     * check whether network is connected before doing data transaction
     * @return true if network is connected
     */
    public static boolean isNetworkAvailable() {
        Context context = CApplication.getAppContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        return ni != null && ni.isConnected();
    }

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        return null != info && info.isAvailable();
    }

    public static int getDisplayHeight(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        return dm.heightPixels;
    }

    public static int getDisplayWidth(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();

        return dm.widthPixels;
    }
}
