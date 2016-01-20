package com.joey.mobilesafe52.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Joey on 2015/11/19.
 */
public class ToastUtils {
    public static void showToast(Context context,String info){
        Toast.makeText(context,info,Toast.LENGTH_SHORT).show();
    }
}
