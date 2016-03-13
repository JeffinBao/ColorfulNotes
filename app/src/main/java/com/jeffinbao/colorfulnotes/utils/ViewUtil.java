package com.jeffinbao.colorfulnotes.utils;

import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.jeffinbao.colorfulnotes.R;

/**
 * Author: baojianfeng
 * Date: 2015-11-25
 */
public class ViewUtil {

    public static boolean viewTouchAlphaChange(View view, MotionEvent event) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.alpha_out);
                view.startAnimation(animation);
                break;
            }
            case MotionEvent.ACTION_UP: {
                Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.alpha_in);
                view.startAnimation(animation);
                break;
            }
        }
        return false;
    }
}
