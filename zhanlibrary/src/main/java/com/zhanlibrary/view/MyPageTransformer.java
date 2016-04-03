package com.zhanlibrary.view;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by bookzhan on 2015/8/14.
 * 最后修改者: bookzhan  version 1.0
 * 说明:
 */
public class MyPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View view, float position) {
        if (position < -1) {
            view.setAlpha(0);
        } else if (position > 1) {
            view.setAlpha(0);
        } else {
            view.setAlpha(1);
            view.setPivotX(position < 0f ? view.getWidth() : 0f);
            view.setPivotY(view.getHeight() * 0.5f);
            view.setRotationY(30f * position);
        }
    }
}
