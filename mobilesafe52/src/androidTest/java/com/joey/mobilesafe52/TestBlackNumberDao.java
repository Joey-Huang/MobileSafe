package com.joey.mobilesafe52;
import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;

import com.joey.mobilesafe52.bean.BlackNumberInfo;
import com.joey.mobilesafe52.db.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * Created by Joey on 2015/12/8.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    private Context mContext;
    private BlackNumberDao dao;
private String TAG="调试TestBlackNumberDao";
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.mContext=getContext();
        dao = new BlackNumberDao(mContext);
    }

    public void testAdd(){
        Random random=new Random();
        for (int i = 0; i < 200; i++) {
            long number=1501160001+i;
            dao.add(number + "", random.nextInt(3) + 1 + "");
        }
    }
    public void testDelete(){
        boolean delete=dao.delete("1501160002");
        assertEquals(true,delete);
    }
    public void testFind(){
        String mode=dao.findNumber("1501160003");
        Log.e(TAG, "1501160003 mode= " + mode);
    }
    public void testFindAll(){
        List<BlackNumberInfo> blackNumberInfos=dao.findAll();
        for (BlackNumberInfo blackNumber :
                blackNumberInfos) {
//            Log.e(TAG, "number= " + blackNumber.getNumber() +
//                    "\nmode" + blackNumber.getMode());
        }
    }
    public void testFindPar(){
        List<BlackNumberInfo>blackNumberInfos=dao.findPar(1, 10);
        for (BlackNumberInfo blackNumber :
                blackNumberInfos) {
            Log.e(TAG, "number= " + blackNumber.getNumber() +
                    "\nmode" + blackNumber.getMode());
        }

    }
    public void testTotalNumber(){
        int totalNumber=dao.getTotalNumber();
        Log.e(TAG, "testTotalNumber="+totalNumber);
    }
}
