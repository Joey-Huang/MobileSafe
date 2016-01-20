package com.joey.mobilesafe52.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.joey.mobilesafe52.db.dao.BlackNumberDao;

public class CallSafeService extends Service {
    private static final String TAG = "调试CallSafeService";
    private BlackNumberDao dao;
    private TelephonyManager tm;
    private InnerReceiver innerReceiver;
    private IntentFilter intentFilter;
    private MyListener listener;

    public CallSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication ch annel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dao = new BlackNumberDao(this);


        //获取电话管理器，监听来电--电话拦截功能搁置
//        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        listener = new MyListener();
//        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);



        //初始化短信广播
        innerReceiver = new InnerReceiver();
        intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(2147483647);
        registerReceiver(innerReceiver, intentFilter);

        Log.e(TAG, "创建服务onCreate ");
    }

    private class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:

                    /**
                     * 黑名单拦截模式
                     * 1.全部拦截 电话拦截+短信拦截
                     * 2.电话拦截
                     * 3.短信拦截
                     */
//
//                    String mode = dao.findNumber(incomingNumber);
//                    if (mode.equals("1") || mode.equals("2")) {
////                        Uri uri=Uri.parse("");
////                        getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler()));
//
////                        endCall();
//                        Log.e(TAG, "黑名单广播-电话铃响-电话被拦截");
//                    }
//                    Log.e(TAG, "黑名单广播-电话铃响" + "  电话号码为：" + incomingNumber + "    拦截模式为：" + mode);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:

                    break;
                default:
                    break;
            }


        }
    }

//    private void endCall() {
//        try {
//            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
//            Method method = clazz.getDeclaredMethod("getService", String.class);
//            IBinder iBinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
//            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
//            iTelephony.endCall();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }



    private class InnerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objects = (Object[]) intent.getExtras().get("pdus");
            for (Object object : objects) {
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                String originatingAddress = message.getOriginatingAddress();
                String body = message.getMessageBody();
                String mode = dao.findNumber(originatingAddress);
                Log.e(TAG, "黑名单广播-短信号码： " + originatingAddress);
                Log.e(TAG, "黑名单广播-短信内容： " + body);
                Log.e(TAG, "黑名单广播-拦截模式： " + mode);
                /**
                 * 黑名单拦截模式
                 * 1.全部拦截 电话拦截+短信拦截
                 * 2.电话拦截
                 * 3.短信拦截
                 */
                if (mode.equals("1") | mode.equals("3")) {
                    Log.e(TAG, "黑名单广播-短信拦截-短信被拦截");
                    abortBroadcast();
                }
                //只能拦截
                if (body.contains("xueshengmei")) {
                    abortBroadcast();
                }
            }
        }
    }

    private class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //解除注册广播，取消电话管理器监听
        unregisterReceiver(innerReceiver);
//        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        Log.e(TAG, "销毁服务onDestroy ");
    }
}
