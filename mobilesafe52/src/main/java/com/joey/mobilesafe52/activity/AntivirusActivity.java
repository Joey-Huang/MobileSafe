package com.joey.mobilesafe52.activity;


import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.db.dao.AntivirusDao;
import com.joey.mobilesafe52.utils.AssetsUtils;
import com.joey.mobilesafe52.utils.MD5Utils;

import java.util.List;
import java.util.Map;

/**
 * Created by Joey on 2015/12/28.
 */
public class AntivirusActivity extends Activity {
    private static final String TAG = "调试AntivirusActivity";
    private static final int BEGIN = 1;
    private static final int SCANNING = 2;
    private static final int END = 3;
    private static final int STOP = 4;
    private static int scanningStatus = END;
    private static int virusCount = 0;
    private static int scanningCount = 0;

    private String scanningResult;

    private ImageView ivScanning;
    private ProgressBar pbScanningProgress;
    private TextView tvScanningDetail;
    private TextView tvScanningResult;
    private Button btBeginScanning;

    private Message message;
    private RotateAnimation rotateAnimation;
    private Button btStopScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        AssetsUtils.copyDB("antivirus.db", this);
    }

    private void initUI() {
        setContentView(R.layout.activity_antivirus);
        /**
         * 通过XML找控件--初始化控件值
         */
        ivScanning = (ImageView) findViewById(R.id.iv_antivirus_scanning);
        pbScanningProgress = (ProgressBar) findViewById(R.id.pb_antivirus_scanning_progress);
        btBeginScanning = (Button) findViewById(R.id.bt_antivirus_begin_scanning);
        btStopScanning = (Button) findViewById(R.id.bt_antivirus_stop_scanning);
        tvScanningDetail = (TextView) findViewById(R.id.tv_antivirus_scanning_detail);
        tvScanningResult = (TextView) findViewById(R.id.tv_antivirus_scanning_result);
        tvScanningDetail.setText("");
        tvScanningResult.setText("");
        btBeginScanning.setText("开始");
        btStopScanning.setText("停止");

        btStopScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击停止扫描--结束扫描
                scanningStatus = END;
                btBeginScanning.setText("开始");
            }
        });

        btBeginScanning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * scanningStatus
                 * 1.开始状态--点击暂停扫描
                 * 2.扫描状态--点击暂停扫描
                 * 3.结束状态--点击开始扫描
                 * 4.停止状态--点击继续扫描
                 */
                if (scanningStatus == STOP) {
                    scanningStatus = SCANNING;
                    btBeginScanning.setText("暂停");
                } else if (scanningStatus == SCANNING) {
                    scanningStatus = STOP;
                    btBeginScanning.setText("开始");
                } else if (scanningStatus == END) {
                    //结束状态下--点击--开始扫描

                    /**
                     * 初始化扫描旋转动画
                     * float fromDegrees, float toDegrees, float pivotX, float pivotY
                     *
                     */
                    rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(2000);
                    rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
                    ivScanning.startAnimation(rotateAnimation);

                    scanningStatus = BEGIN;
                    btBeginScanning.setText("暂停");
                    tvScanningResult.setText("已经扫描了0个应用，发现病毒0个");
                    scanningResult = "正在为你查杀病毒。。。。。。请稍等";
                    tvScanningDetail.setText(scanningResult);
                    initData();
                }


            }
        });
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                PackageManager packageManager = getPackageManager();
                List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);

                /**
                 * 开始扫描
                 * 1.发送进度条最大值
                 */
                int installedPackagesSize = installedPackages.size();
                message = handler.obtainMessage();
                message.arg1 = installedPackagesSize;
                message.what = BEGIN;
                handler.sendMessage(message);

                for (PackageInfo packageInfo : installedPackages) {
                    //判断状态
                    if (scanningStatus == END) {
                        break;
                    }

                    //获取手机的APP名
                    String appName = (String) packageInfo.applicationInfo.loadLabel(packageManager);
                    //获取手机的MD5
                    String sourceDir = packageInfo.applicationInfo.sourceDir;
                    String fileMd5 = MD5Utils.getFileMd5(sourceDir);

                    //根据MD5扫描
                    Map<String, String> virusMap = AntivirusDao.getVirusInfo(fileMd5);
                    virusMap.put("appName", appName);
                    if (virusMap.get("desc") == null) {
                        scanningResult = appName + "。 \t\t\t安全\n" + scanningResult;
                    } else {
                        scanningResult = appName + "。 \t\t\t" + virusMap.get("desc") + "\n" + scanningResult;
                        virusCount++;
                    }
                    try {
                        sleep(500);
                        //暂停状态
                        while (scanningStatus == STOP && scanningStatus != END) {
                            sleep(1000);
                        }


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(SCANNING);
                }
                handler.sendEmptyMessage(END);
            }
        }.start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BEGIN) {
                //开始扫描
                scanningStatus = BEGIN;
                pbScanningProgress.setMax(msg.arg1);
            }
            if (msg.what == SCANNING) {
                if (rotateAnimation.isFillEnabled()) {
                    rotateAnimation.start();
                }


                //扫描中
                scanningStatus = SCANNING;
                scanningCount++;
                tvScanningDetail.setText(scanningResult);
                tvScanningResult.setText("已经扫描了" + scanningCount + "个应用，发现病毒" + virusCount + "个");
                pbScanningProgress.setProgress(pbScanningProgress.getProgress() + 1);
            }
            if (msg.what == END) {
                //扫描结束
                rotateAnimation.cancel();
                btBeginScanning.setText("开始");
                pbScanningProgress.setProgress(0);
                tvScanningDetail.setText("杀毒完成.\n" + scanningResult);

                //还原数据初始值
                virusCount = 0;
                scanningCount = 0;
                scanningStatus = END;
            }

        }
    };


    @Override
    protected void onStop() {
        super.onStop();
        scanningStatus = END;//结束子线程。
        if (rotateAnimation.isInitialized()) {
            rotateAnimation.cancel();
            ivScanning.clearAnimation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
