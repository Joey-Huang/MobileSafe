package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.service.AddressService;
import com.joey.mobilesafe52.service.CallSafeService;
import com.joey.mobilesafe52.service.WatchDogService;
import com.joey.mobilesafe52.utils.ServiceStatusUtils;
import com.joey.mobilesafe52.view.SettingClickView;
import com.joey.mobilesafe52.view.SettingItemView;

/**
 * Created by Joey on 2015/11/14.
 */
public class SettingActivity extends Activity {

    private SettingItemView siv_update;
    private SettingItemView siv_call_safe;
    private SettingItemView sivAddress;
    private SettingClickView scvAddressStyle;
    private SharedPreferences mPref;
    private final String[] items=new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
    private SettingItemView siv_watch_dog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdateView();
        initAddressView();
        initAddressStyle();//修改风格
        initAddressLocation();
        initBlackView();    //初始化黑名单

        initWatchDog();//初始化看门狗
    }

    /**
     * 初始化看门狗
     */
    private void initWatchDog() {
        siv_watch_dog = (SettingItemView) findViewById(R.id.siv_watch_dog);
        boolean serviceRunning=ServiceStatusUtils.isServiceRuning(this,
                "com.joey.mobilesafe52.service.WatchDogService");
        siv_watch_dog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断勾选状态
                if (siv_watch_dog.isChecked()) {
                    siv_watch_dog.setChecked(false);
                    stopService(new Intent(SettingActivity.this, WatchDogService.class));
                    mPref.edit().putBoolean("watch_dog",false).commit();
                } else {
                    siv_watch_dog.setChecked(true);
                    startService(new Intent(SettingActivity.this, WatchDogService.class));
                    mPref.edit().putBoolean("watch_dog",true).commit();
                }
            }
        });
        //根据服务的状态更新勾选框，防止被强行关闭后，仍显示开启。
        if (serviceRunning){
            siv_watch_dog.setChecked(true);
        }else{
            siv_watch_dog.setChecked(false);
        }
    }

    /**
     * 初始化黑名单
     */
    private void initBlackView() {
        siv_call_safe = (SettingItemView) findViewById(R.id.siv_call_safe);
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        boolean autoUpdate=mPref.getBoolean("call_safe_number",true);
        siv_call_safe.setChecked(autoUpdate);
        siv_call_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断勾选状态
                if (siv_call_safe.isChecked()) {
                    siv_call_safe.setChecked(false);
                    mPref.edit().putBoolean("call_safe_number", false).commit();
                    stopService(new Intent(SettingActivity.this, CallSafeService.class));
                } else {
                    siv_call_safe.setChecked(true);
                    mPref.edit().putBoolean("call_safe_number", true).commit();
                    startService(new Intent(SettingActivity.this, CallSafeService.class));
                }
            }
        });
    }

    /**
     * 自动更新开关
     */
    private void initUpdateView(){
        siv_update = (SettingItemView) findViewById(R.id.siv_update);
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        boolean autoUpdate=mPref.getBoolean("auto_update",true);
        siv_update.setChecked(autoUpdate);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断勾选状态
                if (siv_update.isChecked()) {
                    siv_update.setChecked(false);
                    mPref.edit().putBoolean("auto_update", false).commit();
                } else {
                    siv_update.setChecked(true);
                    mPref.edit().putBoolean("auto_update", true).commit();

                }
            }
        });
    }
    /**
     * 初始化归属地开关
     */
    private void initAddressView(){
        sivAddress= (SettingItemView) findViewById(R.id.siv_address);
        boolean serviceRunning=ServiceStatusUtils.isServiceRuning(this,
                "com.joey.mobilesafe52.service.AddressService");
        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断勾选状态
                if (sivAddress.isChecked()) {
                    sivAddress.setChecked(false);
                    stopService(new Intent(SettingActivity.this, AddressService.class));
                    mPref.edit().putBoolean("address_view",false).commit();
                } else {
                    sivAddress.setChecked(true);
                    startService(new Intent(SettingActivity.this, AddressService.class));
                    mPref.edit().putBoolean("address_view",true).commit();
                }
            }
        });
        //根据服务的状态更新勾选框，防止被强行关闭后，仍显示开启。
        if (serviceRunning){
            sivAddress.setChecked(true);
        }else{
            sivAddress.setChecked(false);
        }
    }

    /**
     * 初始化提示框的显示风格
     */
    public void initAddressStyle(){
        scvAddressStyle= (SettingClickView) findViewById(R.id.scv_address_style);
        scvAddressStyle.setTitle("归属地提示风格");
        scvAddressStyle.setDesc("活力橙");
        scvAddressStyle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSingleChooseDailog();
            }
        });

    }

    /**
     * 弹出选择风格的单选框
     */
    private void showSingleChooseDailog() {
        final AlertDialog.Builder builder=new AlertDialog.Builder(SettingActivity.this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle("归属地风格");
        final int style=mPref.getInt("address_style",0);
        scvAddressStyle.setDesc(items[style]);
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPref.edit().putInt("address_style", which).commit();
                scvAddressStyle.setDesc(items[which]);
                dialog.dismiss();//消失
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 初始化归属地显示位置
     */
    private void initAddressLocation(){
        SettingClickView scvAddressLocation= (SettingClickView) findViewById(R.id.scv_address_location);
        scvAddressLocation.setTitle("归属地提示框显示位置");
        scvAddressLocation.setDesc("设置归属地提示框显示位置");
        scvAddressLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this,DragViewActivity.class));
            }
        });
    }
}
