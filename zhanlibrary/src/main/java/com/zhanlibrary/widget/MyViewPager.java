package com.zhanlibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by bookzhan on 2015/7/23.
 * 最后修改者: bookzhan  @version 1.0
 *
 * @description 解决ViewPager设置onTouch的时候不能接收到ActionDown事件
 */
public class MyViewPager extends ViewPager {
    private String TAG = "MyViewPage";
    private MyTouchListener myTouchListener;
    private ActionDownListener actionDownListener;

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (myTouchListener != null) {//由于是自己的View接收不到触摸事件,在这儿通过回调来分发一个事件
            myTouchListener.onMyTouch(event);
        }
        if (actionDownListener != null && MotionEvent.ACTION_DOWN == event.getAction()) {//这个父类在dispatchTouchEvent的时候会把这个事件清除了,这儿用接口回调解决
            actionDownListener.onActionDown();
        }
        return super.dispatchTouchEvent(event);
    }

    public interface MyTouchListener {
        void onMyTouch(MotionEvent event);
    }

    public void requestTouchEvent(MyTouchListener myTouchListener) {
        this.myTouchListener = myTouchListener;
    }

    public interface ActionDownListener {
        void onActionDown();
    }

    public void setOnActionDownListener(ActionDownListener actionDownListener) {
        this.actionDownListener = actionDownListener;
    }


}