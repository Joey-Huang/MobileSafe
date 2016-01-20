package com.joey.mobilesafe52.adapter;

import java.util.List;

/**
 * Created by Joey on 2015/12/23.
 */
public interface MyCallBack<T> {
    void setList(List<T>firstList);
    void setList(List<T>firstList,List<T>secondList);
    void setList(List<T>firstList,List<T>secondList,List<T>thirdList);
}
