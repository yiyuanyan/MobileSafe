package com.hejianxin.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hejianxin.mobilesafe.R;
import com.hejianxin.mobilesafe.utils.StreamUtil;
import com.hejianxin.mobilesafe.utils.ToastUtil;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {
    protected static final String tag = "SplashActivity";
    //更新新版本的状态码
    private static final int UPDATE_VERSION = 100;
    //进入应用程序主界面
    private static final int ENTER_HOME = 101;
    //出错状态码
    private static final int ERROR_ALL = 102;
    private TextView tv_version_name;
    private int mLocalVersionCode;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case UPDATE_VERSION:
                    showUpdateDialog();
                    //ToastUtil.show(SplashActivity.this,"可以更新了");
                    break;
                case ENTER_HOME:
                    enterHome();
                    break;
                case ERROR_ALL:
                    ToastUtil.show(SplashActivity.this,"网络异常");
                    break;
            }
        }
    };

    /**
     * 弹出对话框提示更新
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.show();
    }

    /**
     * 进入APP的主页面
     */
    private void enterHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        //开启新的界面后，将导航界面关闭
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        //初始化UI
        initUI();
        //初始化数据
        initData();

    }

    /**
     * 获取数据
     */
    private void initData() {
        //获取应用版本名称
        String versionName = getVersionName();
        //Toast.makeText(getApplicationContext(), versionName, Toast.LENGTH_SHORT).show();
        tv_version_name.setText("版本名称:"+versionName);
        //获取本地版本号
        mLocalVersionCode = getVersionCode();
        //获取服务器版本号，开启子线程
        checkVersion();

    }

    /**
     * 获取服务器版本号
     */
    private void checkVersion() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                Message msg = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL("http://www.hejianxin.com/android.php");
                    //开启链接
                    HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                    //设置请求超时
                    connection.setConnectTimeout(5000);
                    //设置读取超时
                    connection.setReadTimeout(5000);
                    //请求方式
                    connection.setRequestMethod("GET");
                    //判断是否请求成功
                    if(connection.getResponseCode() == 200){
                        //以流的形式读取数据
                        InputStream is = connection.getInputStream();
                        //流转换为字符串。使用工具类封装
                        String json = StreamUtil.streamToString(is);
                        //解析JSON
                        JSONObject jsonObject = new JSONObject(json);
                        String versionName = jsonObject.getString("versionName");
                        String versionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        String downloadUrl = jsonObject.getString("downloadUrl");
                        //对比版本号
                        if(mLocalVersionCode < Integer.parseInt(versionCode)){
                            //弹出对话框，这里是子线程，要用消息机制
                            msg.what = UPDATE_VERSION;
                        }else{
                            //不用更新，进入APP主页面
                            msg.what = ENTER_HOME;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = ERROR_ALL;
                }finally {
                    long endTime = System.currentTimeMillis();
                    if((endTime - startTime) < 4000){
                        try {
                            Thread.sleep(4000-(endTime-startTime));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //发送消息
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 返回版本号
     * @return  非0获取成功
     */
    private int getVersionCode() {
        //获取包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //从包的管理者对象中获取指定包名的基本信息,0代表基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //获取版本名称
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名称
     * @return 应用版本名称  返回null代表有异常
     */
    private String getVersionName() {
        //获取包管理者对象packageManager
        PackageManager pm = getPackageManager();
        //从包的管理者对象中获取指定包名的基本信息,0代表基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //获取版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
    }
}
