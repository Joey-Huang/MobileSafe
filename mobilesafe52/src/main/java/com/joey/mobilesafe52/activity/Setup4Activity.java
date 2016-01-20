package com.joey.mobilesafe52.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.joey.mobilesafe52.R;

/**
 * Created by Joey on 2015/11/15.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cbProtect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        mPref.edit().putBoolean("configed",true).commit();
        cbProtect = (CheckBox) findViewById(R.id.cb_protect);
        boolean protect=mPref.getBoolean("protect",false);
        cbProtect.setChecked(protect);
        cbProtect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    cbProtect.setText("防盗保护已经开启");
                    mPref.edit().putBoolean("protect",true).commit();
                }else{
                    cbProtect.setText("你没有开启防盗保护");
                    mPref.edit().putBoolean("protect",false).commit();

                }
            }
        });
    }
    public void showPreviousPage() {
        startActivity(new Intent(Setup4Activity.this, Setup3Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);
    }
    public void showNextPage() {
        startActivity(new Intent(Setup4Activity.this,LostFindActivity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }
}
