package com.joey.mobilesafe52.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by Joey on 2015/12/21.
 */
public class TaskInfo {
    /**
     * 图标
     */
    private Drawable icon;

    /**
     * 包名
     * 进程名
     */
    private String packageName;
    private String appName;

    /**
     * 进程占用内存
     */
    private long memorySize;

    /**
     * 是否为用户进程
     */
    private boolean isUserApp;

    /**
     * 是否勾选上
     */
    private boolean isChecked;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public TaskInfo() {
    }

    public TaskInfo(Drawable icon, String packageName, String appName, long memorySize, boolean isUserApp) {
        this.icon = icon;
        this.packageName = packageName;
        this.appName = appName;
        this.memorySize = memorySize;
        this.isUserApp = isUserApp;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(long memorySize) {
        this.memorySize = memorySize;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }
}
