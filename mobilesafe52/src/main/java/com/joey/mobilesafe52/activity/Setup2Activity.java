package com.joey.mobilesafe52.activity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.utils.ToastUtils;
import com.joey.mobilesafe52.view.SettingItemView;

/**
 * Created by Joey on 2015/11/15.
 */
public class Setup2Activity extends BaseSetupActivity {
    private final String TAG="调试Setup2Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        final SettingItemView siv_sim = (SettingItemView) findViewById(R.id.siv_sim);
        boolean sim_bind=getSharedPreferences("config",MODE_PRIVATE).getBoolean("sim_bind",true);
        siv_sim.setChecked(sim_bind);
        siv_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断勾选状态
                if (siv_sim.isChecked()) {
                    //改成不勾选
                    siv_sim.setChecked(false);
                    mPref.edit().putBoolean("sim_bind", false).commit();
                    mPref.edit().putString("sim", null).commit();

                } else {
                    //改成勾选
                    siv_sim.setChecked(true);
                    mPref.edit().putBoolean("sim_bind", true).commit();
                    //保存SIM卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();//获取SIM卡序列号
                    Log.e(TAG, "sim卡点击绑定 ");
                    Log.e(TAG, "sim卡系列号为： " + simSerialNumber);
                    mPref.edit().putString("sim", simSerialNumber).commit();
                }
            }

        });


    }
    public void showPreviousPage() {
        startActivity(new Intent(Setup2Activity.this, Setup1Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }
    public void showNextPage() {
        //强行绑定SIM卡，否则不允许进入下一个界面
        String sim=mPref.getString("sim",null);
        if (TextUtils.isEmpty(sim)){
            ToastUtils.showToast(this,"必须保存SIM卡");
            return;
        }
        startActivity(new Intent(Setup2Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
