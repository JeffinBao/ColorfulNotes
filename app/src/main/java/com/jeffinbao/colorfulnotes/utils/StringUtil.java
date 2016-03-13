package com.jeffinbao.colorfulnotes.utils;

/**
 * Author: baojianfeng
 * Date: 2016-01-05
 */
public class StringUtil {

    public static int getStringCharacterCount(String s) {
        s = s.replaceAll("[^\\x00-\\xff]","aa");

        return s.length();
    }

    public static int getChineseStringCountWithThreshold(String s, int threshold) {
        if (getStringCharacterCount(s) <= threshold) {
            return 0;
        }

        int length = 0;
        int currentCharacterCount = 0;

        for (int i = 0; i < s.length(); i++) {
            if (currentCharacterCount > threshold) {
                break;
            }

            int ascii = Character.codePointAt(s, i);

            if (!(ascii >= 0 && ascii <= 255)) {
                length++;
                currentCharacterCount += 2;
            } else {
                currentCharacterCount++;
            }
        }

        return length;
    }
}
