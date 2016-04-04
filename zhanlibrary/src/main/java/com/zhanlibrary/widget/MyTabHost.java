package com.zhanlibrary.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhanlibrary.R;
import com.zhanlibrary.utils.DensityUtil;


/**
 * Created by zhandalin on 2016-02-27 11:14.
 * 说明: 由于TabHost系列都不支持缓存页面,缓存逻辑都需要自己写, 所以用这个加
 * viewpager来实现首页全部缓存
 */
public class MyTabHost extends LinearLayout {
    private final int[] tabIcons = {R.drawable.selector_toolbar_home,
            R.drawable.selector_toolbar_personal};
    private final String[] tabTitles = new String[]{"教程", "设置"};
    private OnTabChangedListener onTabChangedListener;
    private View currentSelectView;
    private ViewPager mViewPager;

    public MyTabHost(Context context) {
        this(context, null);
    }

    public MyTabHost(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTabHost(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeTabs(context);
    }

    public void initializeTabs(Context context) {
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams layoutParams = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        for (int i = 0; i < tabIcons.length; i++) {
            View tabView = createTabView(context, i);
            tabView.setLayoutParams(layoutParams);
            addView(tabView);
        }
    }

    private View createTabView(Context context, int position) {
        View view = View.inflate(context, R.layout.item_tab, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_icon);
        imageView.setImageDrawable(getResources().getDrawable(tabIcons[position]));
        TextView textView = (TextView) view.findViewById(R.id.tab_title);
        textView.setText(tabTitles[position]);
        TextView badgeView = (TextView) view.findViewById(R.id.badge_view);
        badgeView.setVisibility(View.INVISIBLE);
        if (0 == position) {
            view.setSelected(true);
            currentSelectView = view;
        }
        view.setTag(position);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                currentSelectView.setSelected(false);
                v.setSelected(true);
                currentSelectView = v;
                int tabIndex = (int) v.getTag();
                if (null != mViewPager)
                    mViewPager.setCurrentItem(tabIndex, false);
                if (null != onTabChangedListener)
                    onTabChangedListener.OnTabChanged(tabIndex);
            }
        });
        return view;
    }

    public void setViewPager(ViewPager mViewPager) {
        mViewPager.requestDisallowInterceptTouchEvent(true);
        mViewPager.setOffscreenPageLimit(tabIcons.length);
        this.mViewPager = mViewPager;
    }

    /**
     * @param tabIndex 第几个tab需要改变
     * @param num      要改成几, 当传入的值小于0时候只是显示红点,传0的时候隐藏红点
     */
    public void setTabRedPointNum(int tabIndex, int num) {
        View view = getChildAt(tabIndex);
        TextView badgeView = (TextView) view.findViewById(R.id.badge_view);
        badgeView.setVisibility(VISIBLE);
        ViewGroup.LayoutParams layoutParams = badgeView.getLayoutParams();
        int size = DensityUtil.dip2px(getContext(), 15);//TODO 修改圆圈的大小
        int redSize = DensityUtil.dip2px(getContext(), 3);
        int paddingSize = DensityUtil.dip2px(getContext(), 5);

        badgeView.setPadding(0, 0, 0, 0);
        badgeView.setBackgroundResource(R.drawable.shape_red_badge_background);
        if (num > 99) {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            badgeView.setPadding(paddingSize, 0, paddingSize, 0);
        } else if (num < 0) {
            badgeView.setBackgroundResource(R.drawable.shape_red_background);
            layoutParams.height = redSize;
            layoutParams.width = redSize;
        } else {
            layoutParams.height = size;
            layoutParams.width = size;
        }

        if (num < 0) {
            badgeView.setText("");
        } else if (0 == num) {
            badgeView.setVisibility(INVISIBLE);
        } else {
            if (num > 999) num = 999;
            badgeView.setText(num + "");
        }
    }

    public interface OnTabChangedListener {
        void OnTabChanged(int tabIndex);
    }

    public void setOnTabChangedListener(OnTabChangedListener onTabChangedListener) {
        this.onTabChangedListener = onTabChangedListener;
    }

    /**
     * 手动切换页面
     */
    public void setCurrentPage(int pageNum) {
        View childAt = getChildAt(pageNum);
        if (currentSelectView != childAt) {
            childAt.setSelected(true);
            currentSelectView.setSelected(false);
            currentSelectView = childAt;
        }
        mViewPager.setCurrentItem(pageNum, false);
        if (null != onTabChangedListener)
            onTabChangedListener.OnTabChanged(pageNum);
    }
}
