package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.service.KillProcessService;
import com.joey.mobilesafe52.view.SettingClickView;
import com.joey.mobilesafe52.view.SettingItemView;

/**
 * Created by Joey on 2015/12/24.
 */
public class TaskSettingActivity extends Activity {

    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        setContentView(R.layout.activity_task_setting);
        /**
         * 根据SP更新 是否显示系统进程 的 勾选状态
         */
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        final SettingItemView sivIsShowSystemProcess = (SettingItemView) findViewById(R.id.siv_is_show_system_process);
        final SettingItemView siv_is_kill_process_on_time= (SettingItemView) findViewById(R.id.siv_is_kill_process_on_time);
        final SettingClickView scv_kill_process_interval= (SettingClickView) findViewById(R.id.scv_kill_process_interval);
        sivIsShowSystemProcess.setChecked(mPref.getBoolean("is_show_system_process", false));
        siv_is_kill_process_on_time.setChecked(mPref.getBoolean("is_kill_process_on_time", false));

        //是否显示系统进程-点击监听-存入SP
        sivIsShowSystemProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置修改选中状态
                sivIsShowSystemProcess.setChecked(!sivIsShowSystemProcess.isChecked());
                //存储选中状态
                mPref.edit().putBoolean("is_show_system_process", sivIsShowSystemProcess.isChecked()).commit();
                finish();
            }
        });

        //是否定时清理进程-点击监听-存入SP
        siv_is_kill_process_on_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置修改选中状态
                siv_is_kill_process_on_time.setChecked(!siv_is_kill_process_on_time.isChecked());
                //存储选中状态
                mPref.edit().putBoolean("is_kill_process_on_time", siv_is_kill_process_on_time.isChecked()).commit();

                if(siv_is_kill_process_on_time.isChecked()){
                    startService(new Intent(TaskSettingActivity.this, KillProcessService.class));
                }else{
                    stopService(new Intent(TaskSettingActivity.this, KillProcessService.class));
                }
                finish();
            }
        });

        //设置定时清理时间间隔
        scv_kill_process_interval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
