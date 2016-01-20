package com.joey.mobilesafe52.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;


/**
 * Created by Joey on 2015/12/23.
 */
public class MyManagerAdapter<T> extends BaseAdapter {
    private List<T>firstList;
    private List<T>secondList;
    public void initList(MyCallBack callBack){
        callBack.setList(firstList,secondList);
    }

    @Override
    public int getCount() {
        return 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
