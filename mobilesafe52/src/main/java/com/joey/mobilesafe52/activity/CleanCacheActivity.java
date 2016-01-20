package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;

import com.joey.mobilesafe52.R;

import java.util.List;


public class CleanCacheActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clean_cache);
        initUI();
    }

    private void initUI() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo :
                installedPackages) {
//            getCacheSize(packageInfo);
        }


    }

    private void getCacheSize(PackageInfo packageInfo) throws ClassNotFoundException {
        Class<?> packageManager = getClassLoader().loadClass("PackageManager");
        try {
            packageManager.getDeclaredMethod("getPackageSizeInfo", String.class, PackageStats.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    private class MyI extends PackageStats {

        public MyI(String pkgName) {
            super(pkgName);
        }
    }
}
