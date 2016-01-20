package com.joey.mobilesafe52.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Joey on 2015/11/14.
 * 读取流的工具
 * @author joey
 */
public class StringUtils {
    public static String readFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        int len=0;
        byte[]buf=new byte[1024];
        while ((len=is.read(buf))!=-1){
            out.write(buf,0,len);
        }
        String result=out.toString();
        out.close();
        is.close();
        return result;
    }


}
