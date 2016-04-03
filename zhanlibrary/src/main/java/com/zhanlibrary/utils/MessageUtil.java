package com.zhanlibrary.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhanlibrary.R;


/**
 * Created by zhandalin on 2016-03-08 20:45.
 * 说明:
 */
public class MessageUtil {

    public static void showToastAtCenter(Context context, String message) {
        Toast mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = mToast.getView();
        view.setBackgroundResource(R.drawable.shape_large_corner_rectangle);
        TextView tv = (TextView) view.findViewById(android.R.id.message);
        tv.setTextColor(Color.WHITE);
        mToast.setMargin(0, 0);
        mToast.setText(message);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }

    public static void showToastAtBottom(Context context, String message) {
        Toast mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View view = mToast.getView();
        view.setBackgroundResource(R.drawable.shape_large_corner_rectangle);
        TextView tv = (TextView) view.findViewById(android.R.id.message);
        tv.setTextColor(Color.WHITE);
        mToast.setMargin(0, 0);
        mToast.setText(message);

        mToast.setGravity(Gravity.BOTTOM, 0, DensityUtil.dip2px(context, 50));
        mToast.show();
    }

}
