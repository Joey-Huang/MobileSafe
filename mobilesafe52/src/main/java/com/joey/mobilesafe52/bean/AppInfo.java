package com.joey.mobilesafe52.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Joey on 2015/12/14.
 */
public class AppInfo {
    /**
     * 程序的图片
     */
    private Drawable icon;
    /**
     * 程序的名字
     */
    private String appName;

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", appSize=" + appSize +
                ", userApp=" + userApp +
                ", isRom=" + isRom +
                ", packageName='" + packageName + '\'' +
                '}';
    }

    /**
     * 程序的大小
     */
    private long appSize;
    /**
     * 表示是否为用户App
     * true     用户App
     * false    系统App
     */
    private boolean userApp;

    public AppInfo() {
    }

    public AppInfo(Drawable icon, String appName, long appSize, boolean userApp, boolean isRom, String packageName) {

        this.icon = icon;
        this.appName = appName;
        this.appSize = appSize;
        this.userApp = userApp;
        this.isRom = isRom;
        this.packageName = packageName;
    }

    /**
     * 放置的位置

     */
    private boolean isRom;
    /**
     * 包名
     */
    private String packageName;

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }



    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }

    public boolean isRom() {
        return isRom;
    }

    public void setIsRom(boolean isRom) {
        this.isRom = isRom;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
