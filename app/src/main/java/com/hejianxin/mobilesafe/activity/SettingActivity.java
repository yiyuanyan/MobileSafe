package com.hejianxin.mobilesafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hejianxin.mobilesafe.R;
import com.hejianxin.mobilesafe.utils.ConstantValue;
import com.hejianxin.mobilesafe.utils.SpUtil;
import com.hejianxin.mobilesafe.view.SettingItemView;

/**
 * Created by hejianxin on 2017/10/31.
 */

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initUpdate();
    }

    private void initUpdate() {
        final SettingItemView siv_update = (SettingItemView) findViewById(R.id.siv_update);
        //获取已有的开关状态
        boolean open_update = SpUtil.getBoolean(this, ConstantValue.OPEN_UPDATE, false);
        siv_update.setCheck(open_update);
        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isCheck = siv_update.isCheck();
                siv_update.setCheck(!isCheck);
                SpUtil.putBoolean(getApplication(),ConstantValue.OPEN_UPDATE,!isCheck);
            }
        });
    }

}
