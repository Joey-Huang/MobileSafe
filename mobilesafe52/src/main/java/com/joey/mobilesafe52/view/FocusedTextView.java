package com.joey.mobilesafe52.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 获取焦点的TextView
 * Created by Joey on 2015/11/14.
 */
public class FocusedTextView extends TextView {
    //上下文
    public FocusedTextView(Context context) {
        super(context);
    }
    //上下文，属性
    public FocusedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //上下文，属性，主题
    public FocusedTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 表示有没有获取焦点
     * @return
     * 跑马灯要运行，首先要用此函数判断是否有焦点，true才能生效
     */
    @Override
    public boolean isFocused() {
        return true;
    }
}
