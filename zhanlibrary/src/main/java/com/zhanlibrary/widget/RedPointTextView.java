package com.zhanlibrary.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhanlibrary.R;
import com.zhanlibrary.utils.DensityUtil;


/**
 * Created by zhandalin on 2016-03-20 10:39.
 * 说明:适合用来显示红点数字的控件,会根据红点的大小来调整红色背景的大小,最大显示999,
 * 当小于0的时候只显示一个红点
 */
public class RedPointTextView extends TextView {
    public RedPointTextView(Context context) {
        this(context, null);
    }

    public RedPointTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RedPointTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.shape_red_corner_background);
        setTextColor(Color.WHITE);
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        setGravity(Gravity.CENTER);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);

    }


    public void setMText(int num) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        int size = DensityUtil.dip2px(getContext(), 15);
        int redSize = DensityUtil.dip2px(getContext(), 5);
        int paddingSize = DensityUtil.dip2px(getContext(), 3);
        setPadding(0, 0, 0, 0);
        if(num>999)num=999;
        if (num > 99) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            setPadding(paddingSize, 0, paddingSize, 0);
        } else if (num < 0) {
            layoutParams.height = redSize;
            layoutParams.width = redSize;
        } else {
            layoutParams.height = size;
            layoutParams.width = size;
        }
        setText(num + "");
    }

}
