package com.joey.mobilesafe52.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Joey on 2015/12/8.
 */
public class BlackNumberOpenHelper extends SQLiteOpenHelper {
    /**
     * @param BLACKNUMBER   表名
     */
    public static final String BLACKNUMBER="blacknumber";
    public BlackNumberOpenHelper(Context context) {
        super(context, "safe.db", null, 1);
    }

    /**
     * blacknumber 表名
     * _id 自增长
     * number 电话号码
     * mode 拦截模式：电话拦截 短信拦截
     * @param db
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+BLACKNUMBER+"(_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
