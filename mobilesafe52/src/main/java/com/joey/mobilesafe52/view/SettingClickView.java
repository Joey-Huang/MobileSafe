package com.joey.mobilesafe52.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.mobilesafe52.R;

/**
 * 设置中心的布局
 */
public class SettingClickView extends RelativeLayout {
    private final String NAMESPACE = "com.joey.mobilesafe52";
    private String TAG = "调试SettingClickView";
    private TextView tv_title;
    private TextView tv_desc;
    private ImageView iv;
    private String mTitle;
    private String mDescOn;
    private String mDescOff;
    public SettingClickView(Context context) {
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
    public SettingClickView(Context context, AttributeSet attrs) {

        super(context, attrs);
        initView();
        initAttributeValue(attrs);
    }
    public SettingClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        initAttributeValue(attrs);
    }

    /**
     * 找到item的id
     */
    private void initView() {
        //将自定义的布局文件 设置给当前的settingItemView
        View.inflate(getContext(), R.layout.view_setting_click, this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        iv = (ImageView) findViewById(R.id.iv_jiantou1);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

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
    }
    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setDesc(String desc) {
        tv_desc.setText(desc);
    }
}