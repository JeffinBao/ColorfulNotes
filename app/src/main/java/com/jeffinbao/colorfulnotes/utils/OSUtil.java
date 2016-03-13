package com.jeffinbao.colorfulnotes.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

/**
 * Author: baojianfeng
 * Date: 2015-11-19
 */
public class OSUtil {

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
