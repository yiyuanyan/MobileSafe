package com.hejianxin.mobilesafe.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**能够获取焦点的自定义的TextView
 * Created by hejianxin on 2017/10/30.
 */

public class FocusTextView extends android.support.v7.widget.AppCompatTextView {
    //使用JAVA代码创建控件
    public FocusTextView(Context context) {
        super(context);
    }
    //
    public FocusTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    //重写获取焦点的方法

    @Override
    public boolean isFocused() {
        return true;

    }
}
