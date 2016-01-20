package com.joey.mobilesafe52;

import android.test.AndroidTestCase;
import android.util.Log;

import com.joey.mobilesafe52.db.dao.AntivirusDao;
import com.joey.mobilesafe52.utils.AssetsUtils;

import java.util.Map;

/**
 * Created by Joey on 2015/12/17.
 */
public class MyTest extends AndroidTestCase {
    private static final String TAG = "调试MyTest";

    public void test(){
        AssetsUtils.copyDB("antivirus.db", getContext());
        Map<String, String> virusMap = AntivirusDao.getVirusInfo("f912187d5d686b9ba05d3a970775601dd9e");
        Log.e(TAG, "name="+virusMap.get("name"));
        Log.e(TAG, "desc="+virusMap.get("desc"));
        if (virusMap.get("name")==null){
            Log.e(TAG, "test null");
        }


    }
}
