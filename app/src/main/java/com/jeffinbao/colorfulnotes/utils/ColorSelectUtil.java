package com.jeffinbao.colorfulnotes.utils;

import android.content.Context;

import com.jeffinbao.colorfulnotes.R;

/**
 * Author: baojianfeng
 * Date: 2015-10-18
 */
public class ColorSelectUtil {

    public static int selectColor(Context context, int position) {
        int remainder = position % 7;
        int color = 0;
        switch (remainder) {
            case 0: {
                color = context.getResources().getColor(R.color.red_u1);
                break;
            }
            case 1: {
                color = context.getResources().getColor(R.color.orange_u1);
                break;
            }
            case 2: {
                color = context.getResources().getColor(R.color.yellow_u1);
                break;
            }
            case 3: {
                color = context.getResources().getColor(R.color.green_u1);
                break;
            }
            case 4: {
                color = context.getResources().getColor(R.color.cyan_u1);
                break;
            }
            case 5: {
                color = context.getResources().getColor(R.color.blue_u1);
                break;
            }
            case 6: {
                color = context.getResources().getColor(R.color.purple_u1);
                break;
            }
        }

        return color;
    }
}
