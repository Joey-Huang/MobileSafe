package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.utils.StringUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    private static final int CODE_UPDATE_DAILOG = 1;
    private static final int CODE_URL_ERROR = 2;
    private static final int CODE_NET_ERROR = 3;
    private static final int CODE_JSON_ERROR = 4;
    private static final int CODE_ENTER_HOME = 5;
    private TextView tv_version;
    private TextView tv_progress;//下载进度显示
    private String TAG = "调试SplashActivity";
    private String mVersionName;
    private int mVersionCode;
    private String mDescription;
    private String mDownloadUrl;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_JSON_ERROR:
                    Log.e(TAG, "CODE_JSON_ERROR------JSON文件出错 ");
                    Toast.makeText(SplashActivity.this, "解析错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_NET_ERROR:
                    Log.e(TAG, "CODE_NET_ERROR-----网络连接错误 ");
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_UPDATE_DAILOG:
                    Log.e(TAG, "CODE_UPDATE_DAILOG-----发现更新 ");
                    showUpdateDailog();
                    break;
                case CODE_URL_ERROR:
                    Toast.makeText(SplashActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "CODE_URL_ERROR-----网络连接错误");
                    enterHome();
                    break;
                case CODE_ENTER_HOME:
                    Log.e(TAG, "CODE_ENTER_HOME-----一切正常，进入主界面");
                    enterHome();
                    break;
            }
        }
    };
    private SharedPreferences mPref;
    private RelativeLayout rl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_progress = (TextView) findViewById(R.id.tv_progress);//默认隐藏
        tv_version.setText("版本名：" + getVersionName());
        mPref = getSharedPreferences("config",MODE_PRIVATE);
        copyDB("address.db");
        //判断是否自动更新
        if (mPref.getBoolean("auto_update", true)){
            checkVersion();
        }else{
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME,2000);
        }
        //渐变动画效果
        AlphaAnimation anim=new AlphaAnimation(0.1f,1f);
        anim.setDuration(2000);
        rl_root.startAnimation(anim);
        //创建快捷方式
        createShortCut();
        //更新病毒库
        updateVirus();

    }

    private void updateVirus() {
        //联网获取最新的病毒数据库

    }

    private void createShortCut() {
        Intent shortCutIntent=new Intent();
        /**
         * 1、意图
         * 2、名字
         * 3、长相
         */
        //意图
        shortCutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        //名字
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "黑马手机卫士");
        //长相
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        //是否可以重复创建
        shortCutIntent.putExtra("duplicate",false);

        Intent intent=new Intent();
        intent.setAction("com.joey.mobilesafe52.home");
        intent.addCategory("android.intent.category.DEFAULT");
        shortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,intent);
        sendBroadcast(shortCutIntent);
    }

    /**
     * 获取版本名称
     *
     * @return
     */
    private String getVersionName() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            String versionName = packageInfo.versionName;
            Log.e(TAG, "当前版本：\nversionCode: " + versionCode +
                    "  versionName  " + versionName);
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到相关的包名-异常
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 从服务器进行版本校验
     */
    private void checkVersion() {

        //启动子线程加载
        new Thread() {
            private long startTime = System.currentTimeMillis();
            private HttpURLConnection conn;
            Message msg = mHandler.obtainMessage();

            @Override
            public void run() {
                try {
                    //本机地址用localhost
                    URL url = new URL("http://192.168.0.101:8080/mobilesafe/updata.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    if (conn.getResponseCode() == 200) {
                        InputStream is = conn.getInputStream();
                        String result = StringUtils.readFromStream(is);
                        Log.e(TAG, "读取updata.json " + result);
                        //解析json
                        JSONObject jo = new JSONObject(result);
                        mVersionName = jo.getString("versionName");
                        mVersionCode = jo.getInt("versionCode");
                        mDescription = jo.getString("description");
                        mDownloadUrl = jo.getString("downloadUrl");
                        Log.e(TAG, "json:" + mVersionName + mVersionCode + mDescription + mDownloadUrl);
                        if (mVersionCode > getVersionCode() && getVersionCode() != -1) {//判断是否有更新
                            //说明有更新，弹出升级对话框
                            Log.e(TAG, "mVersionCode:"+mVersionCode+"   getVersionCode():"+getVersionCode());
                            msg.what = CODE_UPDATE_DAILOG;
                        } else {
                            //说明没有更新
                            msg.what = CODE_ENTER_HOME;

                        }

                    }
                } catch (MalformedURLException e) {
                    //url异常
                    msg.what = CODE_URL_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    //网络异常
                    msg.what = CODE_NET_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    //json解析失败
                    msg.what = CODE_JSON_ERROR;
                    Log.e(TAG, "json解析失败-----------------");
                    e.printStackTrace();
                } finally {
                    //保证主界面2秒钟
                    long endTime = System.currentTimeMillis();

                    try {
                        if ((endTime - startTime) < 2000) {
                            sleep(2000 - (endTime - startTime));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 返回本地app版本号
     *
     * @return
     */
    private int getVersionCode() {
        PackageManager packageManager = getPackageManager();
        try {

            PackageInfo packageInfo = packageManager.getPackageInfo(SplashActivity.this.getPackageName(), 0);
            Log.e(TAG, "getPackageName:"+getPackageName());
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            //没有找到相关的包名-异常
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 弹出升级对话框
     */
    protected void showUpdateDailog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("最新版本：" + mVersionName);
        builder.setMessage(mDescription);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e(TAG, "立即更新 onclick--------");
                download();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.show();
    }

    /**
     * 跳转至主页面
     */
    private void enterHome() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 下载apk文件
     */
    protected void download() {
        //XUtils
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String target = Environment.getExternalStorageDirectory() + "/update.apk";
            Log.e(TAG, "下载保存路径： " + target);
            Log.e(TAG, "下载地址： " + mDownloadUrl);
            HttpUtils utils = new HttpUtils();
            tv_progress.setVisibility(View.VISIBLE);
            utils.download(mDownloadUrl, target, new RequestCallBack<File>() {
                //文件的下载进度
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                    Log.e(TAG, "下载进度:   total=" + total + "    current=" + current);
                    tv_progress.setText("下载进度：" + current / total * 100 + "%");
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    Log.e(TAG, "下载成功");
                    Toast.makeText(SplashActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
                    // 跳转到系统下载页面
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setDataAndType(Uri.fromFile(responseInfo.result),
                            "application/vnd.android.package-archive");
                    startActivityForResult(intent, 0);// 如果用户取消安装的话,
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    Log.e(TAG, "下载失败");
                    tv_progress.setText("下载进度：0.00%");
                    Toast.makeText(SplashActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            Log.e(TAG, "无sd卡");
            Toast.makeText(SplashActivity.this, "SD卡读取失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拷贝数据库
     * @param dbName
     */
    private void copyDB(String dbName){
        File destFile=new File(getFilesDir(),dbName);
        Log.e(TAG, "destFile路径 "+destFile.getPath());
        if (destFile.exists()){
            Log.e(TAG, "数据库 "+dbName+"已经存在");
            return;
        }
        InputStream is=null;
        FileOutputStream fos=null;
        try {
           is=getAssets().open(dbName);
            fos=new FileOutputStream(destFile);
            byte[]buffer=new byte[1024];
            int len=0;
            while ((len=is.read(buffer))!=-1){
                fos.write(buffer,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
