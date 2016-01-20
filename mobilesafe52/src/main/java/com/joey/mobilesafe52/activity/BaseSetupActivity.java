package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 不需要注册，因为不许要展示
 * Created by Joey on 2015/11/16.
 */
public abstract class BaseSetupActivity extends Activity {
    public SharedPreferences mPref;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPref=getSharedPreferences("config",MODE_PRIVATE);
        mDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            //监听手势滑动
            /**
             * @param e1        滑动的起点
             * @param e2        滑动的终点
             * @param velocityX 水平速度
             * @param velocityY 垂直速度
             * @return
             */
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                /**
                 * rawX表示整个屏幕
                 * getX表示控件范围内
                 */
                //判断y坐标幅度是否过大
                if (Math.abs((e2.getRawY() - e1.getRawY())) < 60) {
                    if ((e2.getRawX() - e1.getRawX()) > 100) {
                        //向右滑动
                        showPreviousPage();
                        return true;
                    } else if ((e2.getRawX() - e1.getRawX()) < -100) {
                        //向左滑动
                        showNextPage();
                        return true;
                    }
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });
    }

    public void next(View view) {
        showNextPage();
    }

    public void previous(View view) {
        showPreviousPage();
    }
    //手势识别
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);//委托手势识别处理器处理时间
        return super.onTouchEvent(event);
    }

    /**
     * 强制子类必须实现
     */
    public abstract void showPreviousPage();
    public abstract void showNextPage();
}
