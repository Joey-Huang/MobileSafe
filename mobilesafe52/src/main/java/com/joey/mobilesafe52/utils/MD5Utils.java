package com.joey.mobilesafe52.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by Joey on 2015/11/15.
 */
public class MD5Utils {
    private static String TAG="调试MD5Utils";
    /**
     * MD5加密
     *
     * @param password
     * @return
     */
    public static String encode(String password) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(password.getBytes());
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                int i = b & 0xff;
                String hexString=Integer.toHexString(i);
                if (hexString.length()<2){
                    hexString="0"+hexString;
                }
                sb.append(hexString);
            }
            Log.e(TAG, "调用encode()  MD5密码为："+sb.toString());
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getFileMd5(String sourceDir) {
        try {
            File file=new File(sourceDir);
            FileInputStream fis=new FileInputStream(file);
            byte []buf=new byte[1024];
            int len=-1;

            //获取数字摘要
            MessageDigest messageDigest=MessageDigest.getInstance("MD5");
            while ((len=fis.read(buf))!=-1){
                messageDigest.update(buf,0,len);
            }
            byte[]result=messageDigest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                int i = b & 0xff;
                String hexString=Integer.toHexString(i);
                if (hexString.length()<2){
                    hexString="0"+hexString;
                }
                sb.append(hexString);
            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 获取文件的md5值，
     * @param path 文件的路径
     * @return null文件不存在
     */
//    public static String getFileMd5(String path ){
//        try {
//            MessageDigest digest = MessageDigest.getInstance("md5");
//            File file = new File(path);
//            FileInputStream fis = new FileInputStream(file);
//            byte[] buffer = new byte[1024];
//            int len = -1;
//            while ((len = fis.read(buffer)) != -1) {
//                digest.update(buffer,0,len);
//            }
//            byte[] result = digest.digest();
//            StringBuilder sb  =new StringBuilder();
//            for(byte b : result){
//                int number = b&0xff;
//                String hex = Integer.toHexString(number);
//                if(hex.length()==1){
//                    sb.append("0"+hex);
//                }else{
//                    sb.append(hex);
//                }
//            }
//            return sb.toString();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
