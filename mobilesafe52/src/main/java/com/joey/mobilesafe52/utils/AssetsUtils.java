package com.joey.mobilesafe52.utils;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joey on 2015/12/28.
 */
public class AssetsUtils {
    private static final String TAG = "调试AssetsUtils";

    public static void copyDB(String dbName,Context context){
        File destFile = new File(context.getFilesDir(), dbName);
        Log.e(TAG, "destFile路径 " + destFile.getPath());
        if (destFile.exists()) {
            Log.e(TAG, "数据库 " + dbName + "已经存在");
            return;
        }
        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(dbName);
            fos = new FileOutputStream(destFile);
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
