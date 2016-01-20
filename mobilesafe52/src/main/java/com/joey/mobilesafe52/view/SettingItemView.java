package com.joey.mobilesafe52.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.mobilesafe52.R;

/**
 * 设置中心的布局
 */
public class SettingItemView extends RelativeLayout {

    private final String NAMESPACE = "com.joey.mobilesafe52";
    private String TAG = "SettingItemView 调试";
    private TextView tv_title;
    private TextView tv_desc;
    private CheckBox cb_status;
    private String mTitle;
    private String mDescOn;
    private String mDescOff;
    private SharedPreferences sharedPreferences;
    public SettingItemView(Context context) {
        super(context);
        initView();
    }

    /**
     * 1.初始化获取id
     * 2.获取attribute对应的属性值
     * 3.根据属性值引入item布局中
     *
     * @param context
     * @param attrs
     */
    public SettingItemView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initView();
        initAttributeValue(attrs);
    }
    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttributeValue(attrs);
    }

    /**
     * 找到item的id
     */
    private void initView() {

        //将自定义的布局文件 设置给当前的settingItemView
        View.inflate(getContext(), R.layout.view_setting, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        cb_status = (CheckBox) findViewById(R.id.cb_status);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChecked()){
                    cb_status.setChecked(false);
                }else {
                    cb_status.setChecked(true);
                }
            }
        });
    }

    /**
     * 根据attribute值，设置item布局
     * @param attrs
     */
    private void initAttributeValue(AttributeSet attrs){
        mTitle = attrs.getAttributeValue(NAMESPACE, "itemTitle");
        mDescOn = attrs.getAttributeValue(NAMESPACE, "desc_on");
        mDescOff = attrs.getAttributeValue(NAMESPACE, "desc_off");
        tv_title.setText(mTitle);
        if (cb_status.isChecked()) {
            setDesc(mDescOn);
        } else {
            setDesc(mDescOff);
        }
    }
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }

    public boolean isChecked() {
        if (cb_status.isChecked()) {
            return true;
        } else {
            return false;
        }
    }

    public void setChecked(boolean check) {
        cb_status.setChecked(check);

        Log.e(TAG, "setChecked被调用 \n"+"mDescOn:"+mDescOn+"\nmDescOff:"+mDescOff);
        if (check) {
            tv_desc.setText(mDescOn);
        } else {
            tv_desc.setText(mDescOff);
        }
    }

}