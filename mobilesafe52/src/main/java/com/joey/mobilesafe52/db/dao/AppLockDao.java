package com.joey.mobilesafe52.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装已加锁的应用名.
 */
public class AppLockDao {
    private final AppLockOpenHelper helper;

    public AppLockDao(Context context) {
        helper = new AppLockOpenHelper(context);
    }

    public void add(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        db.insert("info", null, values);
        db.close();
    }

    public void delete(String packageName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info", "packagename = ?", new String[]{packageName});
        db.close();
    }

    public boolean find(String packageName) {
        boolean result = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, "packagename = ?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<String> findAll() {
        List<String> packageNames = new ArrayList<String>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("info", new String[]{"packagename"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            packageNames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();
        return packageNames;
    }
}
