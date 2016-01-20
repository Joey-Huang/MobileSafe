package com.joey.mobilesafe52.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 短信备份的工具类
 * Created by Joey on 2015/12/17.
 */
public class SmsUtils {
    /**
     * 备份短信的接口
     */
    public interface BackupCallSms {
        public void before(int count);
        public void onBackup(int progress);
    }
    public static boolean backUpSms(Context context, BackupCallSms callback) {
        /**
         * 备份短信
         * 1.判断当前用户是否有sd卡，如果有：
         * 2.由于权限问题，使用内容观察者
         * 3.写短信-到SD卡
         */
        final String TAG = "调试SmsUtils.backupSms()";
        //判断SD卡状态
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //SD卡未装载
            return false;
        }

        try {
            File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
            FileOutputStream fos = new FileOutputStream(file);

            //获取短信的内容提供者
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms/");
            //type: 1表示接收短信 2表示发送短信
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "type", "body"}, null, null, null);

            int count = cursor.getCount();//短信数目
            callback.before(count);

            //android系统所有有关XML解析都是PULL类型
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos, "utf-8");
            //standalone表示xml是否独立，true表示文件独立
            serializer.startDocument("utf-8", true);
            //第一个参数：命名空间
            serializer.startTag(null, "smss");
            //设置节点短信条数
            serializer.attribute(null, "size", String.valueOf(count));
            int process = 0;
            while (cursor.moveToNext()) {
                String address = cursor.getString(0);
                String date = cursor.getString(1);
                String type = cursor.getString(2);
                String body = cursor.getString(3);
                Log.e(TAG, "address= " + address);
                Log.e(TAG, "date= " + date);
                Log.e(TAG, "type= " + type);
                Log.e(TAG, "body= " + body);
                Log.e(TAG, "-------------------------------------");
                //一条短信短信开始节点
                serializer.startTag(null, "sms");
                //电话号码
                serializer.startTag(null, "address");
                serializer.text(address);
                serializer.endTag(null, "address");

                //时间
                serializer.startTag(null, "date");
                serializer.text(date);
                serializer.endTag(null, "date");

                //类型
                serializer.startTag(null, "type");
                serializer.text(type);
                serializer.endTag(null, "type");

                //内容-加密(密钥，文本)
                serializer.startTag(null, "body");
                String encryptBody=Crypto.encrypt("123",body);
                serializer.text(encryptBody);
                serializer.endTag(null, "body");

                serializer.endTag(null, "sms");
                SystemClock.sleep(200);
                process++;
                callback.onBackup(process);
            }
            serializer.endTag(null, "smss");
            serializer.endDocument();
            cursor.close();
            fos.flush();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
