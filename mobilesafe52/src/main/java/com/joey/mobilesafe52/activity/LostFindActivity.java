package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joey.mobilesafe52.R;

/**
 * Created by Joey on 2015/11/15.
 */
public class LostFindActivity extends Activity {

    private SharedPreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mpref = getSharedPreferences("config",MODE_PRIVATE);
        boolean configed= mpref.getBoolean("configed", false);
        if (configed){
            setContentView(R.layout.activity_lost_find);
            //根据sp更新安全号码
            TextView tvSafePhone= (TextView) findViewById(R.id.tv_safe_phone);
            String safePhone=mpref.getString("safe_phone",null);
            tvSafePhone.setText(safePhone);
            //根据sp更新保护锁
            ImageView ivLock= (ImageView) findViewById(R.id.iv_lock);
            Boolean protect=mpref.getBoolean("protect",false);
            if (protect){
                ivLock.setImageResource(R.drawable.lock);
            }else {
                ivLock.setImageResource(R.drawable.unlock);
            }




        }else{
            startActivity(new Intent(LostFindActivity.this,Setup1Activity.class));
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(LostFindActivity.this, HomeActivity.class));
        finish();
    }

    public void reEnter(View view) {
        mpref.edit().putBoolean("configed", false).commit();
        startActivity(new Intent(LostFindActivity.this, Setup1Activity.class));
        finish();
    }
}
