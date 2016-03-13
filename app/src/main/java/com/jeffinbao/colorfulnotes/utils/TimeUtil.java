package com.jeffinbao.colorfulnotes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Author: baojianfeng
 * Date: 2015-10-27
 */
public class TimeUtil {

    private static final int EXACT_TIME_LENGTH = 16;

    public static String getCurrentTimeString() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String time = sdf.format(new Date(currentTime));
        TimeZone timeZone = TimeZone.getDefault();
        String gmtTimeZone = timeZone.getDisplayName(false, TimeZone.SHORT);

        return time + " " + gmtTimeZone;
    }

    public static String convertTimeLongToString(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String timeString = sdf.format(new Date(time));
        TimeZone timeZone = TimeZone.getDefault();
        String gmtTimeZone = timeZone.getDisplayName(false, TimeZone.SHORT);

        return timeString + " " + gmtTimeZone;
    }


    public static long TimeInStringConvertToTimeInLong(String time) {
        long millSeconds = 0;
        String exactTime = time.substring(0, EXACT_TIME_LENGTH + 1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        try {
            Date d = sdf.parse(exactTime);
            millSeconds = d.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return millSeconds;
    }


}
