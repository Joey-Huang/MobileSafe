package com.joey.mobilesafe52.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.joey.mobilesafe52.activity.PwdActivity;
import com.joey.mobilesafe52.db.dao.AppLockDao;
import com.joey.mobilesafe52.utils.ParameterTable;

import java.util.List;

/**
 * Created by Joey on 2016/1/5.
 */
public class WatchDogService extends Service {

    private static final String TAG = "调试WatchDogService";
    private ActivityManager activityManager;
    private AppLockDao dao;
    private boolean isRunning;
    public static List<String> packageNames;

    /**
     * 正在解锁的包名-和栈顶包名
     */
    private String top_package_name;
    private String mPackageName;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 初始化正在解锁的包名-和栈顶包名
         */
        top_package_name = "";
        mPackageName = getPackageName();

        /**
         * 锁屏状态下结束广播
         */
        //注册广播接收者
        WatchDogReceiver receiver = new WatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        //停止看门狗
        filter.addAction("com.joey.mobilesafe52.STOP_PROTECT");
        /**
         * 1.锁屏-关闭看门狗
         * 2.解锁—开启看门狗
         */

        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);

        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        dao = new AppLockDao(this);
        packageNames = dao.findAll();
        startWatchDog();

    }

    private void startWatchDog() {
        //后台运行，使用子线程，避免阻塞
        new Thread() {
            @Override
            public void run() {
                super.run();
                isRunning = true;
                while (isRunning) {
                    /**
                     * 程序锁实现：
                     * 1.首先获取当前的任务栈
                     * 2.取栈顶任务
                     */
                    //获取正在运行的任务栈
                    List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(10);
                    //获取最上面的进程
                    ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);

                    //获取顶端应用程序的包名
                    top_package_name = runningTaskInfo.topActivity.getPackageName();
                    /**
                     * 同时满足以下条件执行进入解锁界面
                     * 1.栈顶包名和正在解锁的包名 不同
                     * 2.栈顶包名不能为空
                     * 3.栈顶包名不等于自己的包名
                     */
                    if (!top_package_name.equals(ParameterTable.unlocking_package_name) && !top_package_name.equals("") && !top_package_name.equals(mPackageName)) {
                        Log.e(TAG, "正在判断该包名是否为加锁的APP，包名为="+top_package_name);
                        //是否为加锁APP
                        if (isLockApp(top_package_name)) {
                            Log.e(TAG, "看门狗发现该包被锁定，正在进入密码输入界面");
                            //进入输入密码界面
                            Intent intent = new Intent(WatchDogService.this, PwdActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ParameterTable.unlocking_package_name = top_package_name;//初始化正在解锁的包名
                            startActivity(intent);
                        }
                    }
                }
            }
        }.start();
    }

    /**
     * 判断是否为已经加锁的app
     * @param packageName
     * @return
     */
    private boolean isLockApp(String packageName) {
        for (String str : packageNames) {
            if (packageName.equals(str)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    private class WatchDogReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.joey.mobilesafe52.STOP_PROTECT")) {

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                //锁屏-狗休息
                if (isRunning) {
                    isRunning = false;
                }

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                //解锁-狗启动
                if (!isRunning) {
                    isRunning = true;
                }
            }
        }
    }
}
