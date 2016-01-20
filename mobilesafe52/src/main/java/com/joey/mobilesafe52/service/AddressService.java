package com.joey.mobilesafe52.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.joey.mobilesafe52.R;
import com.joey.mobilesafe52.db.dao.AddressDao;

/**
 * Created by Joey on 2015/12/2.
 */
public class AddressService extends Service {
    private final String TAG = "调试AddressService";
    private TelephonyManager tm;
    private MyListener listener;
    private OutCallReceiver receiver;
    private WindowManager mWM;
    private View view;
    private SharedPreferences mPref;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config", MODE_PRIVATE);

        //获取电话管理器，监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);


        //动态注册广播，监听去电
        receiver = new OutCallReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);

        Log.e(TAG, "创建服务onCreate ");
    }


    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话响了
                    Log.e(TAG, "电话铃响了" + "onCallStateChanged");
                    String address = AddressDao.getAddress(incomingNumber);
                    showToast(address);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (mWM != null && view != null) {
                        mWM.removeView(view);
                        view = null;
                    }
                default:
                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    /**
     * 监听去电
     */
    class OutCallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = AddressDao.getAddress(number);
            showToast(address);
        }
    }

    /**
     * 自定义归属地浮窗
     */
    public void showToast(String text) {
        mWM = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.gravity = Gravity.LEFT + Gravity.TOP;//将位置设置为左上方（0,0）

        int style = mPref.getInt("address_style", 0);
        int[] bgs = new int[]{
                R.drawable.call_locate_white,
                R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,
                R.drawable.call_locate_gray,
                R.drawable.call_locate_green};
        SharedPreferences mPref = getSharedPreferences("config", MODE_PRIVATE);
        int lastX = mPref.getInt("lastX", 0);
        int lastY = mPref.getInt("lastY", 80);
        //由于初始位置为左上方，因此使用此方法；
        params.x = lastX;
        params.y = lastY;
        view = View.inflate(this, R.layout.toast_address, null);
        view.setBackgroundResource(bgs[style]);
        TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
        tvNumber.setText(text);
        mWM.addView(view, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //停止来电服务
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        //停止去电广播
        unregisterReceiver(receiver);
        //关闭设备管理器
        if (mWM != null && view != null) {
            mWM.removeView(view);
            view=null;
        }
        Log.e(TAG, "销毁服务onDestroy ");
    }

}
