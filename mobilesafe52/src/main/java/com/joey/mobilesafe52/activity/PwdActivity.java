package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.utils.ParameterTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 2016/1/5.
 */
public class PwdActivity extends Activity {
    private List<Button> buttons;
    private EditText et_pwd_enter;
    private SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwd);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        initUI();
    }

    /**
     * 通过XML找Button--并设置点击监听
     */
    private void initUI() {
        /**
         * 根据mPref设置标题--XML默认设置为 第二个
         * 1.请设置密码
         * 2.请输入密码
         */
        TextView tv_pwd_title = (TextView) findViewById(R.id.tv_pwd_title);
        final String watch_dog_pwd = mPref.getString("watch_dog_pwd", "");
        if (watch_dog_pwd.equals("")) {
            tv_pwd_title.setText("请设置密码");
        }

        /**
         * 通过XML找到数字按钮0-9
         * 通过XML找到 清空 和 删除 按钮
         */
        Button bt_pwd0 = (Button) findViewById(R.id.bt_pwd0);
        Button bt_pwd1 = (Button) findViewById(R.id.bt_pwd1);
        Button bt_pwd2 = (Button) findViewById(R.id.bt_pwd2);
        Button bt_pwd3 = (Button) findViewById(R.id.bt_pwd3);
        Button bt_pwd4 = (Button) findViewById(R.id.bt_pwd4);
        Button bt_pwd5 = (Button) findViewById(R.id.bt_pwd5);
        Button bt_pwd6 = (Button) findViewById(R.id.bt_pwd6);
        Button bt_pwd7 = (Button) findViewById(R.id.bt_pwd7);
        Button bt_pwd8 = (Button) findViewById(R.id.bt_pwd8);
        Button bt_pwd9 = (Button) findViewById(R.id.bt_pwd9);
        //两个按钮  清空 删除
        Button bt_pwd_clear = (Button) findViewById(R.id.bt_pwd_clear);
        Button bt_pwd_delete = (Button) findViewById(R.id.bt_pwd_delete);

        /**
         * 密码输入框    设置不弹出
         */
        et_pwd_enter = (EditText) findViewById(R.id.et_pwd_enter);
        et_pwd_enter.setInputType(InputType.TYPE_NULL);

        /**
         * 将Button存入List列表中
         */
        buttons = new ArrayList<Button>();
        buttons.add(bt_pwd0);
        buttons.add(bt_pwd1);
        buttons.add(bt_pwd2);
        buttons.add(bt_pwd3);
        buttons.add(bt_pwd4);
        buttons.add(bt_pwd5);
        buttons.add(bt_pwd6);
        buttons.add(bt_pwd7);
        buttons.add(bt_pwd8);
        buttons.add(bt_pwd9);
        buttons.add(bt_pwd_clear);
        buttons.add(bt_pwd_delete);

        /**
         * 为0-9设置点击事件
         */
        for (int i = 0; i < buttons.size(); i++) {
            final int finalI = i;
            buttons.get(i).setTextSize(22f);
            buttons.get(i).setBackgroundResource(R.drawable.btn_light_green_selector);
            buttons.get(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = "" + et_pwd_enter.getText().toString();
                    if (finalI <= 9&&str.length()<=3) {
                        et_pwd_enter.setText(str + finalI);
                    } else if (finalI == 10) {
                        et_pwd_enter.setText("");
                    } else if (finalI == 11&&str.length()>0) {
                        et_pwd_enter.setText(str.substring(0, str.length() - 1));
                    }
                }
            });
        }

        /**
         *确认--匹配密码是否正确
         */
        Button bt_pwd_comfirm = (Button) findViewById(R.id.bt_pwd_comfirm);
        bt_pwd_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = "" + et_pwd_enter.getText().toString();
                if (watch_dog_pwd.equals("")) {
                    //设置密码-保存密码
                    mPref.edit().putString("watch_dog_pwd", pwd).commit();
                } else if (!watch_dog_pwd.equals("") && watch_dog_pwd.equals(pwd)) {
                    //密码正确

                } else {
                    //密码错误

                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        ParameterTable.unlocking_package_name="";
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
