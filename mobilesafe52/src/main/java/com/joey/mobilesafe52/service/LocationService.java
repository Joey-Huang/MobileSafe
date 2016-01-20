package com.joey.mobilesafe52.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Joey on 2015/11/25.
 */
public class LocationService extends Service {
    private final String TAG = "调试LocationService";
    private MyLocationListener myListener;
    private LocationManager locationManager;
    private SharedPreferences mPref;

    @Override
    public void onCreate() {
        super.onCreate();
        mPref = getSharedPreferences("config", MODE_PRIVATE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        myListener = new MyLocationListener();
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);//是否允许付费 比如3G网络
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(criteria, true);//获取最佳位置提供者
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        List<String> stringList = locationManager.getAllProviders();
//        for (String s : stringList) {
//            Log.e(TAG, s);
//        }
        locationManager.requestLocationUpdates(bestProvider, 60, 60, myListener);
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            //经度
            String j = String.valueOf(location.getLongitude());
            //维度
            String w = String.valueOf(location.getLatitude());
            //海拔
            String h = String.valueOf(location.getAltitude());
            //准确度
            String jqd = String.valueOf(location.getAccuracy());
            //获取经纬度保存到mPref
            mPref.edit().putString("location", "j:" + j + ";w:" + w).commit();
            Log.e(TAG, "经纬度坐标保存成功 经度："+j+"   维度："+w);
            stopSelf();


        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager.removeUpdates(myListener);
    }
}
