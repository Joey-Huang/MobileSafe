package com.joey.mobilesafe52.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.os.SystemClock;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.bean.TaskInfo;
import com.joey.mobilesafe52.utils.DebugUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 2015/12/21.
 */
public class TaskInfoParser {
    public static List<TaskInfo> getTaskInfos(Context context) {
        List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();

        /**
         *  获取进程列表-RunningAppProcessInfo
         */
        ActivityManager activityManager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessInfos = activityManager.getRunningAppProcesses();

        //获取包管理器-可通过包名获取包对应的信息
        PackageManager packageManager = context.getPackageManager();
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessInfos) {
            TaskInfo taskInfo = new TaskInfo();
            try {
                /**
                 * 获取应用程序包名
                 */
                String packageName = appProcessInfo.processName;

                taskInfo.setPackageName(packageName);
                Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(new int[]{appProcessInfo.pid});

                int totalPrivateDirty = memoryInfos[0].getTotalPrivateDirty() * 1024;

                //获取包信息
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);

                /**
                 * 获取应用程序图标
                 */
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);

                /**
                 * 获取应用程序名
                 */
                String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();

                /**
                 * 是否是用户进程
                 */
                int flags = packageInfo.applicationInfo.flags;
                boolean isUserApp;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //表示系统APP
                    isUserApp = false;
                } else {
                    //表示用户APP
                    isUserApp = true;
                }

                /**
                 * 内存大小
                 */
                //获取应用程序的存放路径
                String sourceDir = packageInfo.applicationInfo.sourceDir;
                File file = new File(sourceDir);
                //获取APK的长度
                long memorySize = file.length();


                //初始化一个进程信息-添加进list
                taskInfo.setPackageName(packageName);
                taskInfo.setAppName(appName);
                taskInfo.setIcon(icon);
                taskInfo.setIsUserApp(isUserApp);
                taskInfo.setMemorySize(totalPrivateDirty);
                taskInfos.add(taskInfo);
            } catch (Exception e) {
                e.printStackTrace();
                //系统信息无法获取
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
                taskInfo.setAppName("系统进程");
                taskInfos.add(taskInfo);
            }
        }
        if (DebugUtils.delayedTime > 0) {
            SystemClock.sleep(DebugUtils.delayedTime);
        }
        return taskInfos;
    }
}
