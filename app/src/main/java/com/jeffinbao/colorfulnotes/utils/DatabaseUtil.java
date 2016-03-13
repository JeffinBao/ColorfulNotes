package com.jeffinbao.colorfulnotes.utils;

import android.content.Context;
import android.database.sqlite.SQLiteException;

import com.jeffinbao.colorfulnotes.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author: baojianfeng
 * Date: 2015-10-15
 */
public class DatabaseUtil {

    private Context context;
    private static String DATABASE_PATH;

    public DatabaseUtil(Context context) {
        this.context = context;
        String packageName = context.getPackageName();
        DATABASE_PATH = "/data/data/" + packageName + "/databases/";
    }

    public boolean isDatabaseExist(String dbName) {
        try {
            String databaseFilename = DATABASE_PATH + dbName;
            File file = new File(databaseFilename);
            if (file.exists()) {
                return true;
            }

        } catch (SQLiteException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void copyDatabase(String desDbName) {
        String desFileName = DATABASE_PATH + desDbName;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists()) {
            dir.mkdir();
        }

        try {
            FileOutputStream fos = new FileOutputStream(desFileName);
            InputStream is = context.getResources().openRawResource(R.raw.colorfulnotes);

            byte[] buffer = new byte[8192];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
