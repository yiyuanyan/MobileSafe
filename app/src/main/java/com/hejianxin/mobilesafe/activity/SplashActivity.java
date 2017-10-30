package com.hejianxin.mobilesafe.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
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
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import org.json.JSONObject;

import java.io.File;
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
    private String mVersionDes;
    private String mdownloadUrl;

    /**
     * 弹出对话框提示更新
     */
    private void showUpdateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.home_apps);
        builder.setTitle("版本更新");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //下载apk
                downloadApk();

            }
        });
        builder.setNegativeButton("稍后再说", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //取消进入主界面
                enterHome();
            }
        });
        //点击取消的事件
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                //取消进入主界面
                enterHome();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void downloadApk() {
        //apk下载链接地址,放置APK的所在路径
        //判断SD卡是否可用
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            //获取SD卡路径
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "app.apk";
            //发送请求获取apk并且放到指定路径
            HttpUtils httpUtils = new HttpUtils();
            httpUtils.download(mdownloadUrl, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    //下载成功
                    Log.i(tag,"下载成功");
                    File file = responseInfo.result;
                    //
                    installApk(file);
                }

                @Override
                public void onFailure(HttpException e, String s) {
                    //下载失败
                    Log.i(tag,"下载失败");
                }
                //刚刚开始下载
                @Override
                public void onStart() {
                    super.onStart();
                    Log.i(tag,"刚刚开始下载");
                }
                //下载过程中的方法

                /**
                 *
                 * @param total 文件总大小
                 * @param current  当前位置
                 * @param isUploading  是否正在下载
                 */
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                }
            });
        }
    }

    /**
     *
     * @param file 安装的文件
     */
    public void installApk(File file){
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源传递过去
        intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
        //startActivity(intent);

        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        enterHome();
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
                        mVersionDes = jsonObject.getString("versionDes");
                        String versionCode = jsonObject.getString("versionCode");
                        mdownloadUrl = jsonObject.getString("downloadUrl");
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
