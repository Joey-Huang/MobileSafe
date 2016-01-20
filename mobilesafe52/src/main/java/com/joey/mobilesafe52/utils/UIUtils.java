package com.joey.mobilesafe52.utils;

import android.app.Activity;

/**
 * Created by Joey on 2015/12/17.
 */
public class UIUtils {
    public static void showToast(final Activity context,final String msg){
        if ("main".equals(Thread.currentThread().getName())){
            ToastUtils.showToast(context,msg);
        }else{
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtils.showToast(context,msg);
                }
            });
        }
    }
}
