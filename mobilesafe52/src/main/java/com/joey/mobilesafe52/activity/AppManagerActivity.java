package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.bean.AppInfo;
import com.joey.mobilesafe52.engine.AppInfos;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;


public class AppManagerActivity extends Activity implements View.OnClickListener {
    private final String TAG = "调试AppManagerActivity";
    @ViewInject(R.id.lv_app_manager)
    private ListView lvAppManager;
    @ViewInject(R.id.tv_rom)
    private TextView tvRom;
    @ViewInject(R.id.tv_sd)
    private TextView tvSD;
    @ViewInject(R.id.tv_app_count)
    private TextView tvAppCount;


    private List<AppInfo> userAppInfo;
    private List<AppInfo> systemAppInfo;
    private List<AppInfo> appInfos;
    private PopupWindow popupWindow;
    private AppInfo clickAppInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            AppManagerAdapter appManagerAdapter = new AppManagerAdapter();
            lvAppManager.setAdapter(appManagerAdapter);
            lvAppManager.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem > userAppInfo.size()) {
                        //系统应用程序
                        tvAppCount.setText("系统程序（" + systemAppInfo.size() + "）");
                    } else {
                        //用户应用程序
                        tvAppCount.setText("用户程序（" + userAppInfo.size() + "）");
                    }
                    setPopupWindowDismiss();
                }
            });
            lvAppManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //获取当前点击item对象
                    Object obj = lvAppManager.getItemAtPosition(position);

                    if (obj != null && obj instanceof AppInfo) {
                        View contentView = View.inflate(AppManagerActivity.this, R.layout.item_popup, null);
                        setPopupWindowDismiss();

                        //-2表示包裹内容
                        popupWindow = new PopupWindow(contentView, -2, -2);

                        //使用popupWindow设置动画，必须设置背景
                        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        //获取view展示在窗体上的位置
                        int[] loction = new int[2];//第一个表示x，第二个表示y
                        view.getLocationInWindow(loction);
                        popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP, 70, loction[1]);
                        ScaleAnimation sa = new ScaleAnimation(0.5f, 1.0f, 0.5f, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                        sa.setDuration(200);
                        contentView.startAnimation(sa);

                        //点击事件uninstall,run,share
                        LinearLayout llUninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
                        LinearLayout llRun = (LinearLayout) contentView.findViewById(R.id.ll_run);
                        LinearLayout llShare = (LinearLayout) contentView.findViewById(R.id.ll_share);
                        LinearLayout llDetail = (LinearLayout) contentView.findViewById(R.id.ll_detail);
                        clickAppInfo = (AppInfo) obj;
                        //注册卸载广播


                        llUninstall.setOnClickListener(AppManagerActivity.this);
                        llRun.setOnClickListener(AppManagerActivity.this);
                        llShare.setOnClickListener(AppManagerActivity.this);
                        llDetail.setOnClickListener(AppManagerActivity.this);
                    }
                }
            });
        }
    };

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                appInfos = AppInfos.getAppInfos(AppManagerActivity.this);

                userAppInfo = new ArrayList<AppInfo>();
                systemAppInfo = new ArrayList<AppInfo>();
                for (AppInfo appInfo : appInfos) {
                    if (appInfo.isUserApp()) {
                        userAppInfo.add(appInfo);
                    } else {
                        systemAppInfo.add(appInfo);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void initUI() {
        setContentView(R.layout.activity_app_manager);
        lvAppManager = (ListView) findViewById(R.id.lv_app_manager);
        ViewUtils.inject(this);
        //获取rom内存的剩余空间
        long rom_freeSpace = Environment.getDataDirectory().getFreeSpace();
        //SD卡的剩余空间
        long sd_freeSpace = Environment.getExternalStorageDirectory().getFreeSpace();


        Log.e(TAG, "内存可用：" + rom_freeSpace);
        Log.e(TAG, "SD卡可用：" + sd_freeSpace);
        tvRom.setText("内存可用:" + Formatter.formatFileSize(this, rom_freeSpace));
        tvSD.setText("SD卡可用:" + Formatter.formatFileSize(this, sd_freeSpace));


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_uninstall:
                Intent uninstallIntent = new Intent();
                uninstallIntent.setAction("android.intent.action.DELETE");
                uninstallIntent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
                uninstallIntent.addCategory(Intent.CATEGORY_DEFAULT);
                this.startActivity(uninstallIntent);
                setPopupWindowDismiss();
                break;
            case R.id.ll_run:
                //运行
                Intent runIntent = this.getPackageManager().getLaunchIntentForPackage(clickAppInfo.getPackageName());
                this.startActivity(runIntent);
                setPopupWindowDismiss();
                break;
            case R.id.ll_share:
                Intent shareIntent = new Intent("android.intent.action.SEND");
                shareIntent.setType("text/plain");
                shareIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                shareIntent.putExtra("android.intent.extra.TEXT", "Hi,小灰灰。。。i o u，请用老公开发的" + clickAppInfo.getAppName() + "软件 下载地址" + "http://play.google.com/store/apps/details?id=" + clickAppInfo.getPackageName());
                this.startActivity(Intent.createChooser(shareIntent, "分享"));
                setPopupWindowDismiss();
                break;
            case R.id.ll_detail:
                Intent detailIntent=new Intent();
                detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detailIntent.addCategory(Intent.CATEGORY_DEFAULT);
                detailIntent.setData(Uri.parse("package:" + clickAppInfo.getPackageName()));
                startActivity(detailIntent);
                break;
            default:
                break;
        }
    }

    private class AppManagerAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return userAppInfo.size() + systemAppInfo.size() + 2;
        }

        @Override
        public Object getItem(int position) {
            if (position == 0 | position == userAppInfo.size() + 1) {
                return null;
            } else if (position < userAppInfo.size() + 1) {
                return userAppInfo.get(position - 1);
            } else {
                return systemAppInfo.get(position - userAppInfo.size() - 2);
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //第一个位置显示用户程序，系统程序第一个位置显示系统程序
            if (position == 0) {
                //用户程序第一个位置
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("用户程序（" + userAppInfo.size() + "）");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            } else if (position == userAppInfo.size() + 1) {
                //系统程序第一个位置
                TextView textView = new TextView(AppManagerActivity.this);
                textView.setText("系统程序（" + systemAppInfo.size() + "）");
                textView.setTextColor(Color.WHITE);
                textView.setBackgroundColor(Color.GRAY);
                return textView;
            }

            ViewHolder holder;
            AppInfo appInfo;

            //设置list
            if (position < userAppInfo.size() + 1) {
                appInfo = userAppInfo.get(position - 1);
            } else {
                appInfo = systemAppInfo.get(position - userAppInfo.size() - 2);
            }

            /**
             * setTag（）逻辑：
             * 如果position=1,表示第二个位置，此时的convertView=textView:            设置Tag
             * 如果position=userAppInfo.size() + 2，此时的convertView=textView:    设置Tag
             *
             */
            if (convertView != null && convertView instanceof LinearLayout) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                convertView = View.inflate(AppManagerActivity.this, R.layout.item_app_manager, null);
                holder.ivAppIcon = (ImageView) convertView.findViewById(R.id.iv_app_icon);
                holder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                holder.tvAppLocation = (TextView) convertView.findViewById(R.id.tv_app_location);
                holder.tvAppSize = (TextView) convertView.findViewById(R.id.tv_app_size);
                convertView.setTag(holder);
            }

            //获取图标
            holder.ivAppIcon.setBackground(appInfo.getIcon());

            //获取程序名
            holder.tvAppName.setText(appInfo.getAppName());

            //获取程序存储位置
            if (appInfo.isUserApp()) {
                holder.tvAppLocation.setText("手机内存");
            } else {
                holder.tvAppLocation.setText("系统内存");
            }

            //获取程序大小
            holder.tvAppSize.setText(Formatter.formatFileSize(AppManagerActivity.this, appInfo.getAppSize()));

            return convertView;
        }

    }

    static class ViewHolder {
        ImageView ivAppIcon;
        TextView tvAppName;
        TextView tvAppLocation;
        TextView tvAppSize;
    }

    public void setPopupWindowDismiss() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    @Override
    protected void onDestroy() {
        setPopupWindowDismiss();
        super.onDestroy();

    }
}
