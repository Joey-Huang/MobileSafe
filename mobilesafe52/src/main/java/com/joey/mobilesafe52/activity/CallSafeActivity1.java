package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
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

public class CallSafeActivity1 extends Activity {
    private final int UPDATELIST = 1;
    private BlackNumberDao dao;
    private List<BlackNumberInfo> blackNumberInfos;
    private ListView lv_black_number;
    private TextView tvPageNumber;
    private EditText etPageNumber;
    private LinearLayout llPb;
    /**
     * 当前页
     */
    private int mCurrentPageNumber = 0;
    /**
     * 煤业数据条数
     */
    private int mPageSize = 10;
    private int totalPage;
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
                    adapter = new MyAdapter(blackNumberInfos, CallSafeActivity1.this);
                    lv_black_number.setAdapter(adapter);
                    tvPageNumber.setText(mCurrentPageNumber + 1 + "\\" + (totalPage + 1));    // 当前页/总页数
                    break;
            }


        }
    };
    private MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_safe);
        initUI();
        initData();
    }

    private void initUI() {
        llPb = (LinearLayout) findViewById(R.id.ll_pb);
        //展示加载圈
        llPb.setVisibility(View.VISIBLE);
        lv_black_number = (ListView) findViewById(R.id.lv_black_number);
        tvPageNumber = (TextView) findViewById(R.id.tv_page_number);
        etPageNumber = (EditText) findViewById(R.id.et_page_number);
        //获取数据库dao
        dao = new BlackNumberDao(CallSafeActivity1.this);
    }

    private void initData() {
       new Thread() {
           @Override
           public void run() {
               super.run();
               totalPage = dao.getTotalNumber() / mPageSize;
               blackNumberInfos=dao.findPar(mCurrentPageNumber, mPageSize);
               handler.sendEmptyMessage(UPDATELIST);
           }
       }.start();
    }

    /**
     * 上一页
     *
     * @param view
     */
    public void prePage(View view) {
        if (mCurrentPageNumber <= 0) {
            ToastUtils.showToast(this, "已经是第一页了");
            return;
        } else {
            mCurrentPageNumber--;
            initData();
        }
    }

    /**
     * 下一页
     *
     * @param view
     */
    public void nextPage(View view) {
        if (mCurrentPageNumber >= totalPage) {
            ToastUtils.showToast(this, "已经是最后一页了");
            return;
        } else {
            mCurrentPageNumber++;
            initData();
        }
    }

    /**
     * 跳转
     *
     * @param view
     */
    public void jump(View view) {
        String pageNumber= etPageNumber.getText().toString();
        int IPageNumber= Integer.parseInt(pageNumber)-1;    //匹配mCurrentPageNumber范围（0-最大）
        if (TextUtils.isEmpty(pageNumber)){
            ToastUtils.showToast(this,"请输入需跳转的页码");
        }else if(IPageNumber>=0&&IPageNumber<=totalPage){
            mCurrentPageNumber=IPageNumber;
            initData();
        }else{
            ToastUtils.showToast(this,"请输入正确的页码");
        }
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
                convertView = View.inflate(CallSafeActivity1.this, R.layout.item_call_safe, null);
                viewHolder = new ViewHolder();
                viewHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
                viewHolder.tvMode = (TextView) convertView.findViewById(R.id.tv_mode);
                viewHolder.ivDelete= (ImageView) convertView.findViewById(R.id.iv_delete);
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
            final BlackNumberInfo info=lists.get(position);
            viewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String number=info.getNumber();
                    boolean result=dao.delete(number);
                    if (result){
                        ToastUtils.showToast(CallSafeActivity1.this,"删除成功");
                        lists.remove(info);
                        //刷新界面
                        adapter.notifyDataSetChanged();

                    }else{
                        ToastUtils.showToast(CallSafeActivity1.this,"删除失败");
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
}
