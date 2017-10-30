package com.hejianxin.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by hejianxin on 2017/10/27.
 */

public class StreamUtil {
    /**
     *流转换成字符串
     * @param is  流对象
     * @return   流转换的字符串
     */
    public static String streamToString(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;
        try {
            while ((temp=is.read(buffer)) != -1){
                bos.write(buffer,0,temp);
            }
            return bos.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                is.close();
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }
}
