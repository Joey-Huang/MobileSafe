package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.fragment.LockFragment;
import com.joey.mobilesafe52.fragment.UnlockFragment;

/**
 * Created by Joey on 2015/12/30.
 */
public class AppLockActivity extends Activity implements View.OnClickListener {
    private static final int RIGHT_CLICKED = 1;
    private static final int LEFT_CLICKED = 2;
    private static int clickStatus;

    private TextView tv_app_lock_lock;
    private TextView tv_app_lock_unlock;
    private android.app.FragmentManager fragmentManager;
    private UnlockFragment unlockFragment;
    private LockFragment lockFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_app_lock);
        //Fragment管理器
        fragmentManager = getFragmentManager();
        //创建Fragment
        unlockFragment = new UnlockFragment();
        lockFragment = new LockFragment();

        /**
         * 左边未加锁，右边已加锁
         */
        tv_app_lock_lock = (TextView) findViewById(R.id.tv_app_lock_lock);
        tv_app_lock_unlock = (TextView) findViewById(R.id.tv_app_lock_unlock);

        /**
         * 初始化点击状态--左边被点击
         */
        new Thread(){
            @Override
            public void run() {
                super.run();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUI(LEFT_CLICKED);
                    }
                });
            }
        }.start();

        tv_app_lock_lock.setOnClickListener(this);
        tv_app_lock_unlock.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_app_lock_lock:
                //右边被电击
                if (clickStatus!=RIGHT_CLICKED){
                    updateUI(RIGHT_CLICKED);
                }
                break;
            case R.id.tv_app_lock_unlock:
                //左边被点击
                if (clickStatus!=LEFT_CLICKED){
                    updateUI(LEFT_CLICKED);
                }
                break;
        }
    }


    /**
     * 根据被点击对象更新UI
     * @param witchClicked
     */
    private void updateUI(int witchClicked) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (witchClicked) {
            case RIGHT_CLICKED:
                /**
                 * 设置TV背景图片-加锁和未加锁-右边被点击
                 */
                tv_app_lock_unlock.setBackgroundResource(R.mipmap.tab_left_default);
                tv_app_lock_lock.setBackgroundResource(R.mipmap.tab_right_pressed);
                fragmentTransaction.replace(R.id.fl_app_lock_content,lockFragment).commit();
                clickStatus=witchClicked;
                break;
            case LEFT_CLICKED:
                /**
                 * 设置TV背景图片-加锁和未加锁-左边被点击
                 */
                tv_app_lock_unlock.setBackgroundResource(R.mipmap.tab_left_pressed);
                tv_app_lock_lock.setBackgroundResource(R.mipmap.tab_right_default);
                fragmentTransaction.replace(R.id.fl_app_lock_content,unlockFragment).commit();
                clickStatus=witchClicked;
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        fragmentManager=null;
        finish();
    }

    @Override
    protected void onDestroy() {
        finish();
        super.onDestroy();

    }
}
