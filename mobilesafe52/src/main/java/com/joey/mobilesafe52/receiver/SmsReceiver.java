package com.joey.mobilesafe52.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.util.Log;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.service.LocationService;

/**
 * Created by Joey on 2015/11/23.
 */
public class SmsReceiver extends BroadcastReceiver {
    private final String TAG = "调试SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        SharedPreferences mPref = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        String safePhone = mPref.getString("safe_phone", "");
        for (Object object : objects) {
            SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
            String originatingAddress = message.getOriginatingAddress();
            String body = message.getMessageBody();
            Log.e(TAG, "短信号码： " + originatingAddress);
            Log.e(TAG, "安全号码： " + safePhone);
            Log.e(TAG, "短信内容： " + body);

            //如果来信号码不是安全号码
            if (!originatingAddress.equals(safePhone)) {
                return;
            }
            if ("#*alarm*#".equals(body)) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f, 1f);
                player.setLooping(true);
                player.start();
                abortBroadcast();
            } else if ("#*location*#".equals(body)) {
                //获取经纬度坐标
                Log.e(TAG, "onReceive #*location*#");
                context.startService(new Intent(context, LocationService.class));
                String location = mPref.getString("location", "");
                abortBroadcast();
            } else if ("#*wipedata*#".equals(body)) {
                //激活设备管理器
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                ComponentName mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
                Intent devIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                devIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                devIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "设备管理器好牛B");
                context.startActivity(devIntent);
                //锁屏--功能被阉割
//                if (mDPM.isAdminActive(mDeviceAdminSample)) {
//                    mDPM.wipeData(DevicePolicyManager.WIPE_RESET_PROTECTION_DATA);
//                }
                //解除激活
                mDPM.removeActiveAdmin(mDeviceAdminSample);
                abortBroadcast();
            } else if ("#*lockscreen*#".equals(body)) {
                //激活设备管理器
                final DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                final ComponentName mDeviceAdminSample = new ComponentName(context, AdminReceiver.class);
                Intent devIntent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                devIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                devIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
                devIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "设备管理器好牛B");
                context.startActivity(devIntent);
                //锁屏
                Thread t1 = new Thread() {
                    @Override
                    public void run() {
                        Log.e(TAG, "run ");
                        while (mDPM.isAdminActive(mDeviceAdminSample)) {
                            try {
                                sleep(30);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (mDPM.isAdminActive(mDeviceAdminSample)) {
                            mDPM.lockNow();
                        }

                    }
                };
                t1.start();
                abortBroadcast();
            }
        }
    }
}
