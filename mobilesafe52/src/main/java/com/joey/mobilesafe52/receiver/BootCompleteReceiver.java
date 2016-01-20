package com.joey.mobilesafe52.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by Joey on 2015/11/23.
 */
public class BootCompleteReceiver extends BroadcastReceiver {
    private final String TAG="调试BootCompleteReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        boolean protect=sp.getBoolean("protect",false);
        if (protect){
            //获取绑定的SIM卡信息
            String sim=sp.getString("sim",null);
            if (!TextUtils.isEmpty(sim)){
                TelephonyManager tm= (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                String currentSim=tm.getSimSerialNumber()+"12313";
                if (sim.equals(currentSim)){
                    Log.e(TAG, "手机安全 ");
                }else{
                    Log.e(TAG, "SIM卡已经被盗，正在发送报警短信 ");
                    String phone=sp.getString("safe_phone","");
                    SmsManager smsManager=SmsManager.getDefault();
                    smsManager.sendTextMessage(phone,null,"SIM is changed",null,null);
                }
            }
        }
    }
}
