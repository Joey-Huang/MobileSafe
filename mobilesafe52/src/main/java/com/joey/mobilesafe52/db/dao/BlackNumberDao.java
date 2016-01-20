package com.joey.mobilesafe52.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.util.Log;

import com.joey.mobilesafe52.bean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 黑名单数据库
 */
public class BlackNumberDao {
    private String TAG = "调试BlackNumberDao";
    private final String BLACKNUMBER = BlackNumberOpenHelper.BLACKNUMBER;
    private final BlackNumberOpenHelper helper;

    public BlackNumberDao(Context context) {
        helper = new BlackNumberOpenHelper(context);
    }

    /**
     * 增加黑名单
     *
     * @param number 黑名单号码
     * @param mode   拦截模式
     */
    public boolean add(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        long rowid = db.insert(BLACKNUMBER, null, values);
        db.close();
        if (rowid == -1) {
            return false;
        } else {
            Log.e(TAG, "表" + BLACKNUMBER + "增加了一条记录");
            return true;
        }
    }

    /**
     * 通过电话号码删除
     *
     * @param number 电话号码
     */
    public boolean delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        int rawNumber = db.delete(BLACKNUMBER, "number = ?", new String[]{number});
        db.close();
        if (rawNumber == 0) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * 通过电话号码去修改模式
     *
     * @param number
     * @return
     */
    public boolean changeNumberMode(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        int rowNumber = db.update(BLACKNUMBER, values, "number = ?", new String[]{number});
        db.close();
        if (rowNumber == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 通过电话号码去查找
     *
     * @param number
     * @return
     */
    public String findNumber(String number) {
        String mode = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(BLACKNUMBER, new String[]{"mode"}, "number = ?", new String[]{number},
                null, null, null);
        if (cursor.moveToNext()) {
            mode = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return mode;
    }

    public List<BlackNumberInfo> findAll() {
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(BLACKNUMBER, new String[]{"number", "mode"}, null,
                null, null, null, null);

        while (cursor.moveToNext()) {
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            blackNumberInfos.add(new BlackNumberInfo(number, mode));
        }
        cursor.close();
        db.close();
        SystemClock.sleep(3000);
        return blackNumberInfos;
    }

    /**
     * 分页加载数据
     *
     * @param pageNumber 表示当前是第几页
     * @param pageSize   表示一夜有多少条数据
     * @return limit 表示限制当前数据
     * offset表示跳过多少条数据
     */
    public List<BlackNumberInfo> findPar(int pageNumber, int pageSize) {
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(pageSize), String.valueOf(pageSize * pageNumber)});
        while (cursor.moveToNext()) {
            blackNumberInfos.add(new BlackNumberInfo(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        db.close();
        return blackNumberInfos;
    }

    /**
     * 分批加载数据
     * @param startIndex
     * @param maxCount
     * @return
     */
    public List<BlackNumberInfo> findPar2(int startIndex, int maxCount) {
        List<BlackNumberInfo> blackNumberInfos = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacknumber limit ? offset ?",
                new String[]{String.valueOf(maxCount), String.valueOf(startIndex)});
        while (cursor.moveToNext()) {
            blackNumberInfos.add(new BlackNumberInfo(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();
        db.close();
        SystemClock.sleep(1000);
        return blackNumberInfos;
    }

    public int getTotalNumber() {
        int totalNumber = 0;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
        cursor.moveToNext();
        totalNumber = cursor.getInt(0);
        cursor.close();
        db.close();
        return totalNumber;
    }
}
