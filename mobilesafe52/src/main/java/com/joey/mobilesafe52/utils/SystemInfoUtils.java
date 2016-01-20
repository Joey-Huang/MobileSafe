package com.joey.mobilesafe52.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by Joey on 2015/12/21.
 */
public class SystemInfoUtils {
    public static int getProcessCount(Context context){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取手机进程-个数
        List<ActivityManager.RunningAppProcessInfo>appProcessInfos=activityManager.getRunningAppProcesses();
        int size=appProcessInfos.size();
        return size;
    }
    public static long getAvailMem(Context context){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取剩余内存
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }
    public static long getTotalMem(Context context){
        ActivityManager activityManager= (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获取剩余内存
        ActivityManager.MemoryInfo memoryInfo=new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.totalMem;
    }
}
