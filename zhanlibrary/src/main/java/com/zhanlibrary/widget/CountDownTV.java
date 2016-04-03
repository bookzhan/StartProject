package com.zhanlibrary.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @Created by zhangyakai
 * @Time 16/3/7 20:15
 */
public class CountDownTV extends TextView {


    private long time = 0;//ms

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (time - 1000 >= 0) {
                time -= 1000;
                postInvalidate();
                sendEmptyMessageDelayed(0, 1000);
            }else {
                if (countDownListener != null) {
                    countDownListener.finish();
                }
            }
        }
    };

    public CountDownTV(Context context) {
        super(context);
    }

    public CountDownTV(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownTV(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (time > 0) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(" HH  :  mm  :  ss ");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            String formatTime = simpleDateFormat.format(time);
            SpannableString spannableString = new SpannableString(formatTime);
            int[] indexs = new int[]{formatTime.indexOf(":"), formatTime.lastIndexOf(":")};
            BackgroundColorSpan all = new BackgroundColorSpan(Color.BLACK);
            spannableString.setSpan(all, 0, formatTime.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            for (int i = 0; i < indexs.length; i++) {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.BLACK);
                BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.TRANSPARENT);
                spannableString.setSpan(foregroundColorSpan, indexs[i] - 1, indexs[i] + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(backgroundColorSpan, indexs[i] - 1, indexs[i] + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            setText(spannableString);
        }
    }

    public void setTime(long time) {
        if (time>0) {
            this.time = time;
            if (!handler.hasMessages(0)) {
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }
    }

    public interface CountDownListener {
        void finish();
    }

    CountDownListener countDownListener = null;

    public void setCountDownListener(CountDownListener countDownListener) {
        this.countDownListener = countDownListener;
    }
}
