package com.hejianxin.mobilesafe.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by hejianxin on 2017/10/27.
 */

public class ToastUtil {
    @SuppressLint("WrongConstant")
    public static void show(Context ctx, String msg){
        Toast.makeText(ctx,msg,0).show();
    }
}
