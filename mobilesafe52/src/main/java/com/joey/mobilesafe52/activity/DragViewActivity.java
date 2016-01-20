package com.joey.mobilesafe52.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joey.mobilesafe52.R;

/**
 * Created by Joey on 2015/12/3.
 */
public class DragViewActivity extends Activity {

    private ImageView ivDrag;
    private TextView tvTop;
    private TextView tvBottom;
    private int startX;
    private int startY;
    private SharedPreferences mPref;
    private int winHeight;
    private int winWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_view);
        ivDrag = (ImageView) findViewById(R.id.iv_drag);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        winWidth = getWindowManager().getDefaultDisplay().getWidth();
        winHeight = getWindowManager().getDefaultDisplay().getHeight();
        final long[] mHits = new long[2];
        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 80);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        layoutParams.leftMargin = lastX;
        layoutParams.topMargin = lastY;
        ivDrag.setLayoutParams(layoutParams);
        if (ivDrag.getTop() < (winHeight - 20) / 2) {
            tvTop.setVisibility(View.INVISIBLE);
            tvBottom.setVisibility(View.VISIBLE);
        } else {
            tvTop.setVisibility(View.VISIBLE);
            tvBottom.setVisibility(View.INVISIBLE);
        }
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        int dx = endX - startX;
                        int dy = endY - startY;
                        int l = ivDrag.getLeft() + dx;
                        int r = ivDrag.getRight() + dx;
                        int t = ivDrag.getTop() + dy;
                        int b = ivDrag.getBottom() + dy;

                        //更新界面
                        if (l >= 0 && r <= winWidth && t >= 0 && b <= winHeight - 20) {
                            mPref.edit().putInt("lastX", ivDrag.getLeft()).commit();
                            ivDrag.layout(l, t, r, b);
                        }
                        //实现说明框上下显示
                        if (t < (winHeight - 20) / 2) {
                            tvTop.setVisibility(View.INVISIBLE);
                            tvBottom.setVisibility(View.VISIBLE);
                        } else {
                            tvTop.setVisibility(View.VISIBLE);
                            tvBottom.setVisibility(View.INVISIBLE);
                        }
                        //初始化起点坐标
                        startX = endX;
                        startY = endY;
                        break;
                    case MotionEvent.ACTION_UP:
                        //鼠标抬起后记录当前位置
                        mPref.edit().putInt("lastY", ivDrag.getTop()).commit();
                        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                        if (mHits[0] > (mHits[mHits.length - 1] - 500)) {
                            ivDrag.layout(winWidth / 2 - ivDrag.getWidth() / 2, winHeight / 2 - ivDrag.getHeight() / 2,
                                    winWidth / 2 + ivDrag.getWidth() / 2, winHeight / 2 + ivDrag.getHeight() / 2);
                        }
                    default:
                        break;
                }
                return true;
            }

        });
    }
}
