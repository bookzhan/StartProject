package com.zhanlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by zhandalin on 2016-03-21 09:37.
 * 说明:
 */
public class InterceptEventScrollView extends ScrollView {
    private boolean ifNeedEvent;

    public InterceptEventScrollView(Context context) {
        this(context, null);
    }

    public InterceptEventScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterceptEventScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setIfNeedEvent(boolean ifNeedEvent) {
        this.ifNeedEvent = ifNeedEvent;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ifNeedEvent) {
            return super.dispatchTouchEvent(ev);
        } else {
            return true;
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (ifNeedEvent) {
            return super.onTouchEvent(e);
        } else {
            return true;
        }
    }


}
