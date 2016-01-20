package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.db.dao.AddressDao;

/**
 * Created by Joey on 2015/11/26.
 */
public class AddressActivity extends Activity {
    private final String TAG = "调试AddressActivity";
    private EditText etNumber;
    private TextView tvResult;
    private String ss;
    private String sss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        etNumber = (EditText) findViewById(R.id.et_number);
        tvResult = (TextView) findViewById(R.id.tv_result);
        etNumber.addTextChangedListener(new TextWatcher() {
            //文字变化前的回调
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //文字变化的回调
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    Animation shake = AnimationUtils.loadAnimation(AddressActivity.this, R.anim.shake);
                    shake.setInterpolator(new Interpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return (float) Math.sin(input * Math.PI * 7);
                        }
                    });
                    etNumber.startAnimation(shake);
                } else {
                    String result = AddressDao.getAddress(s.toString());
                    tvResult.setText(result);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    /**
     * 开始查询
     *
     * @param view
     */
    public void queryAddress(View view) {
        if (TextUtils.isEmpty(etNumber.getText().toString())) {
            Animation shake = AnimationUtils.loadAnimation(AddressActivity.this, R.anim.shake);
            shake.setInterpolator(new Interpolator() {
                @Override
                public float getInterpolation(float input) {
                    return (float) Math.sin(input * Math.PI * 7);
                }
            });
            etNumber.startAnimation(shake);
        } else {
            String result = AddressDao.getAddress(etNumber.getText().toString());
            tvResult.setText(result);
        }
    }

    /**
     * 手机震动 需要权限
     */
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //等待1秒震动2秒等待1秒震动3秒，-1表示不循环
        vibrator.vibrate(new long[]{1000, 2000, 1000, 3000}, -1);
        //取消震动
        vibrator.cancel();

    }

}
