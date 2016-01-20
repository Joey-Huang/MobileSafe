package com.joey.mobilesafe52.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.utils.ToastUtils;

/**
 * Created by Joey on 2015/11/15.
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText etPhone;
    private final String TAG="调试Setup3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        etPhone = (EditText) findViewById(R.id.et_phone);
        String phone=mPref.getString("safe_phone","");
        etPhone.setText(phone);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case RESULT_OK:
                String phone = data.getStringExtra("phone").replaceAll("-", "").replaceAll(" ", "");
                etPhone.setText(phone);
                break;
        }
        Log.i(TAG, "onActivityResult:\nrequestCode="+requestCode+"\nresultCode="+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 拿到电话号码
     */


    public void showPreviousPage() {
        startActivity(new Intent(Setup3Activity.this,Setup2Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_previous_in, R.anim.tran_previous_out);

    }
    public void showNextPage() {
        /**
         * 点击下一步
         * 1.判断安全号码是否为空
         * 2.如果为空，则不跳转，如果不为空，则将安全号码存入mpref
         *
         */
        String phone=etPhone.getText().toString();
        if (TextUtils.isEmpty(phone.trim())){
            ToastUtils.showToast(this,"安全号码不能为空");
            return;
        }
        mPref.edit().putString("safe_phone",phone).commit();
        /**
         * 进入下一步
         */
        startActivity(new Intent(Setup3Activity.this,Setup4Activity.class));
        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    public void selectContact(View view) {
        Intent intent=new Intent(Setup3Activity.this,ContactActivity.class);
        startActivityForResult(intent,1);
    }

}
