package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.service.AddressService;
import com.joey.mobilesafe52.service.CallSafeService;
import com.joey.mobilesafe52.utils.MD5Utils;

/**
 * Created by Joey on 2015/11/14.
 */
public class HomeActivity extends Activity {
    private final String TAG = "调试HomeActivity";
    private GridView gv_home;
    private String[] mItems = new String[]{
            "手机防盗", "通讯卫士", "软件管家",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"
    };
    private int[] mPics = new int[]{
            R.mipmap.home_safe, R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
            R.mipmap.home_taskmanager, R.mipmap.home_netmanager, R.mipmap.home_trojan,
            R.mipmap.home_sysoptimize, R.mipmap.home_tools, R.mipmap.home_settings
    };
    private SharedPreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mpref = getSharedPreferences("config", MODE_PRIVATE);
        gv_home = (GridView) findViewById(R.id.gv_home);
        gv_home.setAdapter(new HomeAdapt());
        gv_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        //手机防盗
                        showPasswordDialog();
                        break;
                    case 1:
                        //通讯卫士
                        startActivity(new Intent(HomeActivity.this, CallSafeActivity2.class));
                        break;
                    case 2:
                        //软件管理
                        startActivity(new Intent(HomeActivity.this, AppManagerActivity.class));
                        break;
                    case 3:
                        //进程管理
                        startActivity(new Intent(HomeActivity.this,TaskManagerActivity.class));
                        break;
                    case 5:
                        //手机杀毒
                        startActivity(new Intent(HomeActivity.this,AntivirusActivity.class));
                        break;
                    case 6:
                        //缓存清理
                        startActivity(new Intent(HomeActivity.this,CleanCacheActivity.class));
                        break;
                    case 7:
                        //高级工具
                        startActivity(new Intent(HomeActivity.this, AToolsActivity.class));
                        break;
                    case 8:
                        //设置中心
                        startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                        break;
                }
            }
        });
        initData();


    }

    private void initData() {
        boolean call_safe_number = mpref.getBoolean("call_safe_number", false);
        if (call_safe_number) {
            startService(new Intent(this, CallSafeService.class));
        }

        boolean address_view=mpref.getBoolean("address_view",false);
        if (address_view){
            startService(new Intent(HomeActivity.this, AddressService.class));
        }


    }

    private void showPasswordDialog() {
        //判断是否设置密码
        //如果没有设置，弹出设置密码的弹窗
        String password = mpref.getString("password", null);
        if (TextUtils.isEmpty(password)) {
            Log.e(TAG, "手机防盗-进入密码设置界面 ");
            showPasswordSetDialog();
        } else {
            Log.e(TAG, "手机防盗-进入密码输入界面 ");
            showPasswordInputDialog();
        }
    }

    private void showPasswordInputDialog() {
        View view = View.inflate(this, R.layout.dialog_password, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view, 0, 0, 0, 0);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String savedPassword = mpref.getString("password", null);
                if (!TextUtils.isEmpty(password)) {
                    if (MD5Utils.encode(password).equals(savedPassword)) {
                        Toast.makeText(HomeActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "手机防盗-密码输入界面-(登陆成功)进入手机防盗主界面");
                        startActivity(new Intent(HomeActivity.this, LostFindActivity.class));
                        dialog.dismiss();
                        return;
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空，请输入密码", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void showPasswordSetDialog() {
        View view = View.inflate(this, R.layout.dialog_set_password, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(view, 0, 0, 0, 0);
        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etPassword = (EditText) view.findViewById(R.id.et_password);
        final EditText etPasswordConfirm = (EditText) view.findViewById(R.id.et_password_confirm);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = etPassword.getText().toString();
                String passwordConfirm = etPasswordConfirm.getText().toString();
                if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(passwordConfirm)) {
                    if (password.equals(passwordConfirm)) {
                        Toast.makeText(HomeActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "手机防盗-密码设置界面-（密码设置成功）进入密码输入界面");
                        mpref.edit().putString("password", MD5Utils.encode(password)).commit();
                        showPasswordInputDialog();
                        dialog.dismiss();
                        return;
                    } else {
                        Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                }
                Log.e(TAG, "手机防盗-密码设置界面-（密码设置失败）返回主界面");
                dialog.dismiss();
            }
        });
        dialog.show();


    }

    class HomeAdapt extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return mItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
            ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);
            tv_item.setText(mItems[position]);
            iv_item.setImageResource(mPics[position]);
            return view;
        }
    }


}
