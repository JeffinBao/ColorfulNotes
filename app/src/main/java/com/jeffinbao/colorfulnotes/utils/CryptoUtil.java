package com.jeffinbao.colorfulnotes.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Author: baojianfeng
 * Date: 2016-02-03
 */
public class CryptoUtil {

    public static String md5(String plainText) {
        MessageDigest md;
        byte[] result = null;

        try {
            md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            result = md.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return new String(Base64.encode(result, Base64.DEFAULT));
    }
}
