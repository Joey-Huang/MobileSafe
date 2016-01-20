package com.joey.mobilesafe52.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by Joey on 2015/12/2.
 */
public class ServiceStatusUtils {
    /**
     * 检测服务是否运行
     *
     * @return
     */
    public static boolean isServiceRuning(Context context,String serviceName) {
        String TAG="调用isServiceRuning";
        ActivityManager am = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        //获取系统所有正在运行的服务，最多返回100个
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
            String className =serviceInfo.service.getClassName();
            Log.e(TAG, "所有正在运行的服务列表："+className);
            if (serviceName.equals(className)){
                return true;
            }
        }
        return false;
    }
}
