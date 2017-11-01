package com.hejianxin.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by talkgogo on 2017/11/1.
 */

public class SpUtil {

    private static SharedPreferences sp;

    //写入

    /**
     * 
     * @param ctx 上下文
     * @param key 存储节点名称
     * @param value 存储节点的值
     */
    public static void putBoolean(Context ctx,String key,boolean value){
        //存储节点文件名称,读写方式
        if(sp == null) {
            sp = ctx.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();
    }
    //读取
    public static boolean getBoolean(Context ctx,String key,boolean defValue){
        if(sp == null){
            sp = ctx.getSharedPreferences("config",Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,defValue);
    }
}
