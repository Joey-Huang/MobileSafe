package com.joey.mobilesafe52.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.joey.mobilesafe52.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 2015/12/14.
 */
public class AppInfos {
    private static final String TAG = "调试AppInfo";

    public static List<AppInfo> getAppInfos(Context context) {
        List<AppInfo> appInfos = new ArrayList<AppInfo>();


        //获取到包的管理者
        PackageManager packageManager = context.getPackageManager();
        //获取所有的应用程序
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        for (PackageInfo installedPackage : installedPackages) {
            AppInfo appInfo=new AppInfo();

            //获取到应用程序的图标
            Drawable drawable = installedPackage.applicationInfo.loadIcon(packageManager);
            appInfo.setIcon(drawable);

            //获取应用程序的名字
            String apkName = installedPackage.applicationInfo.loadLabel(packageManager).toString();
            appInfo.setAppName(apkName);

            //获取应用程序的包名
            String packageName = installedPackage.packageName;
            appInfo.setPackageName(packageName);

            //获取应用程序的存放路径
            String sourceDir = installedPackage.applicationInfo.sourceDir;

            File file = new File(sourceDir);

            //获取APK的长度
            long apkSize = file.length();
            appInfo.setAppSize(apkSize);

            //是否为用户APP
            int flags=installedPackage.applicationInfo.flags;
            if ((flags& ApplicationInfo.FLAG_SYSTEM)!=0){
                //表示系统APP
                appInfo.setUserApp(false);
            }else{
                //表示用户APP
                appInfo.setUserApp(true);
            }

            //
            if ((flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
                //表示在sd卡
                appInfo.setIsRom(false);
            }else {
                //表示内存
                appInfo.setIsRom(true);
            }
            appInfos.add(appInfo);
        }
        return appInfos;
    }
}
