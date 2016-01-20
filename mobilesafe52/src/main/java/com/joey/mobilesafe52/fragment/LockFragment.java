package com.joey.mobilesafe52.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.bean.AppInfo;
import com.joey.mobilesafe52.db.dao.AppLockDao;
import com.joey.mobilesafe52.engine.AppInfos;
import com.joey.mobilesafe52.utils.ParameterTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joey on 2016/1/3.
 */
public class LockFragment extends Fragment{
    private static final int UPDATE_LIST_VIEW = 1;
    private ListView lv_lock_fragment_detail;
    private ViewHolder viewHolder;
    private AppLockDao dao;
    private List<AppInfo> lockInfos;
    private MyAdapter adapter;
    private TextView tv_lock_fragment_total;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lock, null);
        lv_lock_fragment_detail = (ListView) view.findViewById(R.id.lv_lock_fragment_detail);
        tv_lock_fragment_total = (TextView) view.findViewById(R.id.tv_lock_fragment_total);
        dao = new AppLockDao( getActivity());
        return view;
    }

    @Override
    public void onStart() {
        initFragment();
        super.onStart();
    }

    /**
     * 初始化碎片
     */
    private void initFragment() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                List<AppInfo> appInfos = AppInfos.getAppInfos(getActivity());
                lockInfos = new ArrayList<AppInfo>();
                for (AppInfo appInfo :
                        appInfos) {
                    String name=appInfo.getPackageName();
                    /**
                     * 如果再数据库种，说明已经加锁
                     */
                   if(dao.find(name)){
                       lockInfos.add(appInfo);
                   }
                }
                handler.sendEmptyMessage(UPDATE_LIST_VIEW);
            }
        }.start();
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == UPDATE_LIST_VIEW) {
                //设置ListView适配器
                adapter = new MyAdapter();
                lv_lock_fragment_detail.setAdapter(adapter);
            }
        }
    };


    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            tv_lock_fragment_total.setText("已经加锁("+lockInfos.size()+")个");
            return lockInfos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView==null){
                View v = View.inflate(getActivity(), R.layout.item_lock_fragment, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_app_icon= (ImageView) v.findViewById(R.id.iv_item_unlock_app_icon);
                viewHolder.iv_image= (ImageView) v.findViewById(R.id.iv_item_lock_image);
                viewHolder.tv_app_name= (TextView) v.findViewById(R.id.tv_item_unlock_app_name);
                convertView=v;
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
            final AppInfo appInfo=lockInfos.get(position);
            viewHolder.iv_app_icon.setImageDrawable(appInfo.getIcon());
            viewHolder.tv_app_name.setText(appInfo.getAppName());
            final View finalConvertView = convertView;
            viewHolder.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //初始化位移动画
                    TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                    translateAnimation.setDuration(ParameterTable.lock_fragment_translate_animation_duration);
                    finalConvertView.startAnimation(translateAnimation);
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(ParameterTable.lock_fragment_translate_animation_duration);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //点击加锁---更新数据 界面
                                    String name = appInfo.getPackageName();
                                    dao.delete(name);
                                    lockInfos.remove(appInfo);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }.start();
                }
            });
            return convertView;
        }
    }
    static class ViewHolder{
        ImageView iv_app_icon;
        TextView tv_app_name;
        ImageView iv_image;
    }
}
