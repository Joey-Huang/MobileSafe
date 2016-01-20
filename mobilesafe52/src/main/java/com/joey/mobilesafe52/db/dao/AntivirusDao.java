package com.joey.mobilesafe52.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Joey on 2015/12/28.
 */
public class AntivirusDao {
    private static final String PATH = "data/data/com.joey.mobilesafe52/files/antivirus.db";
    private static final String TAG = "调试AntivirusDao";

    public static Map<String, String> getVirusInfo(String MD5) {
        Map<String, String> virus = new HashMap();
        SQLiteDatabase virusDB = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        /**
         * 字段顺序
         * name 病毒名
         * desc 病毒描述
         */
        Cursor cursor = virusDB.rawQuery("select name,desc from datable where md5 = ?", new String[]{MD5});
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            String desc = cursor.getString(1);
            Log.e(TAG, "name:" + name);
            Log.e(TAG, "desc:" + desc);
            virus.put("name", name);
            virus.put("desc",desc);
        }
        return virus;
    }


}
