package com.zhanlibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhandalin on 2016-03-29 13:39.
 * 说明:
 */
public class InterceptEventListView extends XListView {
    private boolean ifNeedEvent;

    public InterceptEventListView(Context context) {
        this(context, null);
    }

    public InterceptEventListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterceptEventListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
