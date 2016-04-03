package com.zhanlibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.zhanlibrary.R;


/**
 * 自定义Toast
 * Created by zhandalin on 2015/6/18.
 */
public class ToastUtils {

    private static Toast toast;
    private static long oneTime = 0;
    private static long twoTime = 0;
    private static String oldMsg;

    public static void showSuccessToast(Context context, String content) {
//        View toastRoot = View.inflate(context, R.layout.myfavorite_toast, null);
//        TextView tv = (TextView) toastRoot.findViewById(R.id.tv_msg);
//        tv.setText(content);
//        Toast toast = new Toast(context);
//        toast.setGravity(Gravity.CENTER, 0, 0);
//        toast.setView(toastRoot);
//        toast.show();
    }

    /**
     * 防止多次点击的Toast
     * @param context
     * @param content
     */
    public static void showToastAtCenter(Context context, String content) {
        hiddenKeyboard(context);
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.getView().setBackgroundResource(R.drawable.shape_large_corner_rectangle);
            toast.show();
            oneTime = SystemClock.elapsedRealtime();
        } else {
            twoTime = SystemClock.elapsedRealtime();
            if (content.equals(oldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    toast.show();
                }
            } else {
                oldMsg = content;
                toast.setText(content);
                toast.show();
            }
        }
        oneTime = twoTime;
    }

    public static void hiddenKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(((Activity) context).getWindow().getDecorView().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
