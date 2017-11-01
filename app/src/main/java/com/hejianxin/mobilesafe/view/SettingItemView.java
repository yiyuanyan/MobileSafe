package com.hejianxin.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hejianxin.mobilesafe.R;

/**
 * Created by hejianxin on 2017/10/31.
 */

public class SettingItemView extends RelativeLayout {

    private CheckBox cb_box;
    private TextView tv_title;
    private TextView tv_des;

    public SettingItemView(Context context) {
        this(context,null);
    }

    public SettingItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //xml布局转换view
        View.inflate(context, R.layout.settingitem,this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_des = (TextView) findViewById(R.id.tv_des);
        cb_box = (CheckBox) findViewById(R.id.cb_box);

    }

    /**
     *
     * @return 返回当前SettingItemView是否选中状态 true开启 false关闭
     */
    public boolean isCheck(){
        return cb_box.isChecked();
    }

    /**
     *
     * @param isCheck 是否作为开启的变量，有点击过程中传递
     */
    public void setCheck(boolean isCheck){
        cb_box.setChecked(isCheck);
        if(isCheck){
            tv_des.setText("自动更新已开启");
        }else{
            tv_des.setText("自动更新已关闭");
        }
    }
}
