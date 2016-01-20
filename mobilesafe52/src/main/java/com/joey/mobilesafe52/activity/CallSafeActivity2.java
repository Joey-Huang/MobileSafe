package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.adapter.MyBaseAdapter;
import com.joey.mobilesafe52.bean.BlackNumberInfo;
import com.joey.mobilesafe52.db.dao.BlackNumberDao;
import com.joey.mobilesafe52.utils.ToastUtils;

import java.util.List;

public class CallSafeActivity2 extends Activity {
    private final String TAG = "调试CallSafeActivity2";
    private final int UPDATELIST = 1;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> blackNumberInfos;
    private ListView lv_black_number;
    private LinearLayout llPb;

    /**
     *
     */
    private int mStartIndex = 0;   //起始位置
    private int maxCount = 20;    //每批数据条数


    /**
     * 消息队列
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                /**
                 * 更新ListView数据
                 */
                case UPDATELIST:
                    if (llPb.getVisibility() != View.INVISIBLE) {
                        llPb.setVisibility(View.INVISIBLE);
                    }
                    if (adapter != null) {
//                        adapter.setLists(blackNumberInfos);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter = new MyAdapter(blackNumberInfos, CallSafeActivity2.this);
                        lv_black_number.setAdapter(adapter);
                    }
                    break;
            }


        }
    };
    private MyAdapter adapter;
    private int totalNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe2);
        initUI();
        initData();
    }

    private void initUI() {
        llPb = (LinearLayout) findViewById(R.id.ll_pb);
        //展示加载圈
        llPb.setVisibility(View.VISIBLE);
        lv_black_number = (ListView) findViewById(R.id.lv_black_number);

        /**
         *
         * @param view
         * @param scrollState   表示滚动的状态
         */
        lv_black_number.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                        int lastVisiblePosition = lv_black_number.getLastVisiblePosition();
                        if (lastVisiblePosition >= totalNumber - 1) {
                            ToastUtils.showToast(CallSafeActivity2.this, "已经是最后一条数据了");
                            return;
                        }else if(lastVisiblePosition==blackNumberInfos.size()-1) {
                            mStartIndex += maxCount;
                            llPb.setVisibility(View.VISIBLE);
                            initData();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                        break;
                    default:
                        break;
                }


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        //获取数据库dao
        dao = new BlackNumberDao(CallSafeActivity2.this);
        totalNumber = dao.getTotalNumber();
    }

    private void initData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (blackNumberInfos == null) {
                    blackNumberInfos = dao.findPar2(mStartIndex, maxCount);
                } else {
                    blackNumberInfos.addAll(dao.findPar2(mStartIndex, maxCount));
                }
                handler.sendEmptyMessage(UPDATELIST);
            }
        }.start();
    }

    /**
     * 添加黑名单
     *
     * @param view
     */
    public void addBlackNumber(View view) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        View dialogAddBlackNumber = View.inflate(this, R.layout.dialog_add_black_number, null);
        dialog.setView(dialogAddBlackNumber,0,0,0,0);
        final EditText etBlackNumber = (EditText) dialogAddBlackNumber.findViewById(R.id.et_black_number);
        Button btn_ok = (Button) dialogAddBlackNumber.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) dialogAddBlackNumber.findViewById(R.id.btn_cancel);
        final CheckBox cbPhone= (CheckBox) dialogAddBlackNumber.findViewById(R.id.cb_phone);
        final CheckBox cbSms= (CheckBox) dialogAddBlackNumber.findViewById(R.id.cb_sms);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_number = etBlackNumber.getText().toString();
                if (TextUtils.isEmpty(str_number)) {
                    ToastUtils.showToast(CallSafeActivity2.this, "请输入黑名单号码");
                    return;
                }
                String mode = "";
                if (cbPhone.isChecked() && cbSms.isChecked()) {
                    mode = "1";
                } else if (cbPhone.isChecked()) {
                    mode = "2";
                } else if (cbSms.isChecked()) {
                    mode = "3";
                } else {
                    ToastUtils.showToast(CallSafeActivity2.this, "请勾选拦截模式");
                    return;
                }
                /**
                 * 将数据添加至数据库，同时添加至List 用于更新UI
                 */
                dao.add(str_number, mode);
                blackNumberInfos.add(new BlackNumberInfo(str_number, mode));
                if (adapter != null) {
                    adapter.setLists(blackNumberInfos);
                    adapter.notifyDataSetChanged();
                } else {
                    adapter = new MyAdapter(blackNumberInfos, CallSafeActivity2.this);
                    lv_black_number.setAdapter(adapter);
                }
                totalNumber=dao.getTotalNumber();
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * 适配器
     */
    private class MyAdapter extends MyBaseAdapter<BlackNumberInfo> {
        public MyAdapter(List<BlackNumberInfo> lists, Context context) {
            super(lists, context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(CallSafeActivity2.this, R.layout.item_call_safe, null);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                viewHolder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                viewHolder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            BlackNumberInfo blackNumberInfo = lists.get(position);
            viewHolder.tvNumber.setText(blackNumberInfo.getNumber());
            if (blackNumberInfo.getMode().equals("1")) {
                viewHolder.tvMode.setText("来电拦截和短信拦截");
            } else if (blackNumberInfo.getMode().equals("2")) {
                viewHolder.tvMode.setText("来电拦截");
            } else if (blackNumberInfo.getMode().equals("3")) {
                viewHolder.tvMode.setText("短信拦截");
            }
            /**
             * 删除数据功能
             */
            final BlackNumberInfo info = lists.get(position);
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number = info.getNumber();
                    boolean result = dao.delete(number);
                    if (result) {
                        ToastUtils.showToast(CallSafeActivity2.this, "删除成功");
                        lists.remove(info);
                        //刷新界面
                        adapter.notifyDataSetChanged();

                    } else {
                        ToastUtils.showToast(CallSafeActivity2.this, "删除失败");
                    }
                }
            });
            return convertView;
        }

    }

    static class ViewHolder {
        TextView tvNumber;
        TextView tvMode;
        ImageView ivDelete;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CallSafeActivity2.this, HomeActivity.class));
        finish();
    }
}
