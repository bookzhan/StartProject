package com.zhanlibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.zhanlibrary.R;
import com.zhanlibrary.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by zhandalin on 2012/3/1.
 * 说明:页面指示器,支持最后一个指示器是一个圆形的图片
 */
public class PageIndicator extends LinearLayout implements ViewPager.OnPageChangeListener {

    private Context context;
    private int pageCount;
    private int lastPosition = 0;
    private int marginLeft = 10;//dp
    private List<ImageView> pageIndicatorList = new ArrayList<>();
    private LayoutParams params;

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        marginLeft = DensityUtil.dip2px(context, marginLeft);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(marginLeft, 0, 0, 0);
    }

    /**
     * @param pageCount             页面的总数
     * @param lastIndicatorImageUrl 如果没有的话就传null
     */
    public void setPageCount(int pageCount, String lastIndicatorImageUrl) {
        this.pageCount = pageCount;
        if (pageCount <= 1) {
            return;
        }
        pageIndicatorList.clear();
        removeAllViews();
        for (int i = 0; i < pageCount; i++) {
            ImageView imageView;
            if (null != lastIndicatorImageUrl && i + 1 == pageCount) {
                imageView = new CircleImageView(context);
                int size = DensityUtil.dip2px(context, 30);
                LayoutParams layoutParams = new LayoutParams(size, size);
                imageView.setLayoutParams(layoutParams);
                layoutParams.setMargins(marginLeft, 0, 0, 0);
                Picasso.with(context).load(lastIndicatorImageUrl).into(imageView);

                addView(imageView);
            } else {
                imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.selector_page_indicator);
                if (i == 0)
                    addView(imageView);
                else
                    addView(imageView, params);
            }
            imageView.setEnabled(false);
            pageIndicatorList.add(imageView);
        }
        pageIndicatorList.get(lastPosition % pageCount).setEnabled(true);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (pageCount <= 1) {
            return;
        }
        pageIndicatorList.get(lastPosition % pageCount).setEnabled(false);
        pageIndicatorList.get(position % pageCount).setEnabled(true);
        lastPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
