package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.bean.TaskInfo;
import com.joey.mobilesafe52.engine.TaskInfoParser;
import com.joey.mobilesafe52.utils.SystemInfoUtils;
import com.joey.mobilesafe52.utils.ToastUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends Activity implements View.OnClickListener {
    @ViewInject(R.id.tv_task_process_count)
    private TextView tvTaskProcessCount;
    @ViewInject(R.id.tv_task_memory)
    private TextView tvTaskMemory;
    @ViewInject(R.id.lv_task_manager)
    private ListView lvTaskManager;
    @ViewInject(R.id.ll_task_loading)
    private LinearLayout llTaskLoading;
    @ViewInject(R.id.tv_task_process_type)
    private TextView tvTaskProcessType;

    @ViewInject(R.id.bt_select_all)
    private Button btSelectAll;
    @ViewInject(R.id.bt_select_none)
    private Button btSelectNone;
    @ViewInject(R.id.bt_clear)
    private Button btClear;
    @ViewInject(R.id.bt_setting)
    private Button btSetting;

    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;

    private final int WHAT_UPDATE_LIST_VIEW = 1 << 0;
    private MyTaskListAdapt myTaskListAdapt;
    private SharedPreferences mPref;
    private int userProcessCount;
    private int systemProcessCount;
    private boolean cb_task_setting_status;
    private boolean is_show_system_process;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //更新listview
            if ((msg.what & WHAT_UPDATE_LIST_VIEW) != 0) {

                //设置加载不可见
                if (llTaskLoading.getVisibility() == View.VISIBLE) {
                    llTaskLoading.setVisibility(View.INVISIBLE);
                }

                /**
                 * 设置进程总数显示和内存占用显示
                 * 设置lvTaskManager适配器
                 * 设置滚动监听
                 * 设置item点击监听
                 * 初始化button点击事件
                 */
                //获取总内存和剩余内存
                String availMem = Formatter.formatFileSize(TaskManagerActivity.this, SystemInfoUtils.getAvailMem(TaskManagerActivity.this));
                String totalMem = Formatter.formatFileSize(TaskManagerActivity.this, SystemInfoUtils.getTotalMem(TaskManagerActivity.this));
                //设置tv统计：剩余/总内存 和 运行中的进程-并显示
                tvTaskMemory.setText("剩余/总内存：" + availMem + "/" + totalMem);
                tvTaskProcessCount.setText("运行中的进程：" + (userTaskInfos.size() + systemTaskInfos.size()) + "个");
                setUIVisibility(View.VISIBLE);


                //设置lvTaskManager适配器
                myTaskListAdapt = new MyTaskListAdapt();
                lvTaskManager.setAdapter(myTaskListAdapt);


                //滚动监听
                lvTaskManager.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        /**
                         * 根据滚动设置 进程类型
                         */
                        if (firstVisibleItem <= userTaskInfos.size()) {
                            tvTaskProcessType.setText("用户进程(" + userTaskInfos.size() + ")个");
                        } else {
                            tvTaskProcessType.setText("系统进程(" + systemTaskInfos.size() + ")个");
                        }
                    }
                });

                //点击监听
                lvTaskManager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Object object = lvTaskManager.getItemAtPosition(position);
                        if (object != null && object instanceof TaskInfo) {
                            TaskInfo taskInfo = (TaskInfo) object;
                            //如果位自己的进程，点击不生效
                            if (taskInfo.getPackageName().equals(getPackageName())){
                                return;
                            }
                            //判断是否被勾选上
                            ViewHolder holder = (ViewHolder) view.getTag();
                            if (taskInfo.isChecked()) {
                                taskInfo.setIsChecked(false);
                                holder.cbTaskClear.setChecked(false);
                            } else {
                                taskInfo.setIsChecked(true);
                                holder.cbTaskClear.setChecked(true);
                            }
                            view.setTag(holder);
                        }
                    }
                });

                //初始化button点击时间
                btSelectAll.setOnClickListener(TaskManagerActivity.this);
                btSelectNone.setOnClickListener(TaskManagerActivity.this);
                btClear.setOnClickListener(TaskManagerActivity.this);
                btSetting.setOnClickListener(TaskManagerActivity.this);
            }

        }
    };

    /**
     * 主要完成UI数据更新
     */
    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                taskInfos = TaskInfoParser.getTaskInfos(TaskManagerActivity.this);
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();

                /**
                 * 将数据拆分成系统进程和用户进程
                 */
                for (TaskInfo taskInfo :
                        taskInfos) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.add(taskInfo);
                    } else {
                        systemTaskInfos.add(taskInfo);
                    }
                }

                handler.sendEmptyMessage(WHAT_UPDATE_LIST_VIEW);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_select_all:
                //全选
                for (TaskInfo taskInfo : userTaskInfos) {
                    //判断是否为自己的程序
                    if (taskInfo.getPackageName().equals(getPackageName())) {
                        continue;
                    }
                    taskInfo.setIsChecked(true);
                }
                for (TaskInfo taskInfo : systemTaskInfos) {
                    taskInfo.setIsChecked(true);
                }
                myTaskListAdapt.notifyDataSetChanged();
                break;
            case R.id.bt_select_none:
                //反选
                for (TaskInfo taskInfo : userTaskInfos) {
                    if (taskInfo.isChecked()) {
                        taskInfo.setIsChecked(false);
                    } else {
                        taskInfo.setIsChecked(true);
                    }
                }
                for (TaskInfo taskInfo : systemTaskInfos) {
                    if (taskInfo.isChecked()) {
                        taskInfo.setIsChecked(false);
                    } else {
                        taskInfo.setIsChecked(true);
                    }
                }
                myTaskListAdapt.notifyDataSetChanged();
                break;
            case R.id.bt_clear:
                //清理
                ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                //进程总数
                int totalCount = userTaskInfos.size() + systemTaskInfos.size();
                //清理进程个数
                int count = 0;
                //清理的进程大小
                long memorySize = 0;

                /**
                 * 集合在迭代的时候不能修改集合大小！！！！
                 */
                //集合盒子-将需要删除的数据存入盒子，迭代完后再删除
                List<TaskInfo> killLists = new ArrayList<TaskInfo>();


                for (TaskInfo taskInfo : userTaskInfos) {
                    if (taskInfo.isChecked()) {
                        activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                        killLists.add(taskInfo);
                    }
                }
                for (TaskInfo taskInfo : systemTaskInfos) {
                    if (taskInfo.isChecked()) {
                        killLists.add(taskInfo);
                    }
                }

                for (TaskInfo taskInfo : killLists) {
                    if (taskInfo.isUserApp()) {
                        userTaskInfos.remove(taskInfo);
                    } else {
                        systemTaskInfos.remove(taskInfo);
                    }
                    activityManager.killBackgroundProcesses(taskInfo.getPackageName());
                    count++;
                    memorySize += taskInfo.getMemorySize();

                }


                //弹出土司
                ToastUtils.showToast(this, "共清理了" + count + "个进程，释放了" + Formatter.formatFileSize(this, memorySize) + "内存");

                myTaskListAdapt.notifyDataSetChanged();
                tvTaskProcessCount.setText("运行中的进程：" + (totalCount - count) + "个");

                //获取总内存和剩余内存
                String availMem = Formatter.formatFileSize(this, SystemInfoUtils.getAvailMem(this));
                String totalMem = Formatter.formatFileSize(this, SystemInfoUtils.getTotalMem(this));
                tvTaskMemory.setText("剩余/总内存：" + availMem + "/" + totalMem);
                break;
            case R.id.bt_setting:
                //设置
                startActivity(new Intent(this, TaskSettingActivity.class));
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myTaskListAdapt != null) {
            myTaskListAdapt.notifyDataSetChanged();
        }
    }

    public class MyTaskListAdapt extends BaseAdapter {

        private ViewHolder holder;

        @Override
        public int getCount() {
            if (mPref.getBoolean("is_show_system_process", false)) {
                return userTaskInfos.size() + systemTaskInfos.size() + 2;
            } else {
                return userTaskInfos.size() + 1;
            }
        }

        @Override
        public Object getItem(int position) {
            if (position == 0) {
                return null;
            } else if (position == userTaskInfos.size() + 1) {
                return null;
            }

            TaskInfo taskInfo;
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
            }
            return taskInfo;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (position == 0) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("用户进程(" + userTaskInfos.size() + ")个");
                return tv;
            } else if (position == userTaskInfos.size() + 1) {
                TextView tv = new TextView(TaskManagerActivity.this);
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.GRAY);
                tv.setText("系统进程(" + systemTaskInfos.size() + ")个");
                return tv;
            }

            TaskInfo taskInfo;
            //设置系统进程和用户进程的info数据
            if (position < userTaskInfos.size() + 1) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - userTaskInfos.size() - 2);
            }

            if (convertView instanceof LinearLayout && convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                holder = new ViewHolder();
                View view = View.inflate(TaskManagerActivity.this, R.layout.item_task_manager, null);
                holder.ivTaskIcon = (ImageView) view.findViewById(R.id.iv_task_icon);
                holder.tvTaskName = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tvTaskMemorySize = (TextView) view.findViewById(R.id.tv_task_memory_size);
                holder.cbTaskClear = (CheckBox) view.findViewById(R.id.cb_task_clear);
                convertView = view;
                convertView.setTag(holder);
            }
            holder.ivTaskIcon.setBackground(taskInfo.getIcon());
            holder.tvTaskName.setText(taskInfo.getAppName());
            holder.tvTaskMemorySize.setText("内存占用：" + Formatter.formatFileSize(TaskManagerActivity.this, taskInfo.getMemorySize()));

            //判断是否为自己的进程-如果是则让CB按钮隐藏
            if (taskInfo.getPackageName().equals(getPackageName())) {
                holder.cbTaskClear.setVisibility(View.INVISIBLE);
            } else {
                if (taskInfo.isChecked()) {
                    holder.cbTaskClear.setChecked(true);
                } else {
                    holder.cbTaskClear.setChecked(false);
                }
            }

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView ivTaskIcon;
        TextView tvTaskName;
        TextView tvTaskMemorySize;
        CheckBox cbTaskClear;
    }

    /**
     * 主要完成UI状态更新
     */
    private void initUI() {
        setContentView(R.layout.activity_task_manager);
        ViewUtils.inject(this);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        //根据SP，是否显示系统进程
        is_show_system_process = mPref.getBoolean("is_show_system_process", false);

        //设置tv进程类别-加载显示状态
        llTaskLoading.setVisibility(View.VISIBLE);
        tvTaskProcessType.setBackgroundColor(Color.GRAY);
        tvTaskProcessType.setTextColor(Color.WHITE);

        //隐藏UI-需要跟新数据的UI
        setUIVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        myTaskListAdapt = null;
        super.onDestroy();
    }

    private void setUIVisibility(int visibility) {
        tvTaskMemory.setVisibility(visibility);
        tvTaskProcessCount.setVisibility(visibility);
        tvTaskProcessType.setVisibility(visibility);
    }

}
