package com.joey.mobilesafe52.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 归属地查询工具
 */
public class AddressDao {
    //该路径必须是这个目录
    private static final String PATH = "data/data/com.joey.mobilesafe52/files/address.db";

    public static String getAddress(String number) {
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        //手机号码1+（3,4,5,6,7）+9位数字
        //正则表达式
        //^1[3-8]\d{9}
        String address = "未知号码";
        if (number.matches("^1[3-8]\\d{9}")) {
            //匹配手机号码
            Cursor cursor = database.rawQuery("select location from data2 where id =(" +
                    "select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
            if (cursor.moveToNext()) {
                address = cursor.getString(0);
            } else {
                address = "数据正在努力更新中。。。。暂无此归属地";
            }
            cursor.close();
        } else if (number.matches("\\d+$")) {
            //表示匹配数字
            switch (number.length()) {
                case 3:
                    //报警电话
                    address = "报警电话";
                    break;
                case 4:
                    //模拟器
                    address = "模拟器";
                    break;
                case 5:
                    //客服电话
                    address = "客服电话";
                    break;
                //本地电话
                case 7:
                case 8:
                    address = "本地电话";
                    break;
                default:
                    //01088888888
                    if (number.startsWith("0") && number.length() >= 10) {
                        //长途电话 有些区号是4位 有些是3位 都查
                        Cursor cursor = database.rawQuery("select location from data2 where area = ?",
                                new String[]{number.substring(1, 4)});
                        if (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        } else {
                            cursor.close();
                            cursor = database.rawQuery("select location from data2 where area = ?",
                                    new String[]{number.substring(1, 3)});
                            if (cursor.moveToNext()) {
                                address = cursor.getString(0);
                            }
                        }
                        cursor.close();
                    }
                    break;
            }
        }
        database.close();
        return address;
    }
}
