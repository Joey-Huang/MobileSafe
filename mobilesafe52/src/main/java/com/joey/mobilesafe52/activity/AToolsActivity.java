package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.utils.SmsUtils;
import com.joey.mobilesafe52.utils.UIUtils;

/**
 * Created by Joey on 2015/11/25.
 */
public class AToolsActivity extends Activity {
    private ProgressBar pbBackupSms;

    private ProgressDialog mDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
        pbBackupSms= (ProgressBar) findViewById(R.id.pb_backup_sms);
    }

    /**
     * 归属地查询
     *
     * @param view
     */
    public void numberAddressQuery(View view) {
        startActivity(new Intent(this, AddressActivity.class));
    }

    /**
     * 备份短信
     *
     * @param view
     */
    public void backupSms(View view) {
//        mDialog = new ProgressDialog(AToolsActivity.this);
//        mDialog.setTitle("提示");
//        mDialog.setMessage("正在备份短信......");
//        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        mDialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean result = SmsUtils.backUpSms(AToolsActivity.this, new SmsUtils.BackupCallSms() {
                    @Override
                    public void before(int count) {
                        pbBackupSms.setMax(count);
                    }

                    @Override
                    public void onBackup(int progress) {
                        pbBackupSms.setProgress(progress);
                    }
                });
                if (result) {
                    UIUtils.showToast(AToolsActivity.this, "备份成功");
                } else {
                    UIUtils.showToast(AToolsActivity.this, "备份失败");
                }
            }
        }.start();
    }

    public void appLock(View view) {
        startActivity(new Intent(AToolsActivity.this, AppLockActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
