package com.zhanlibrary.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.zhanlibrary.R;
import com.zhanlibrary.adapter.CarouselAdapter;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.http.GHSHttpClient;
import com.zhanlibrary.http.GHSHttpHandler;
import com.zhanlibrary.model.HomeBasesData;
import com.zhanlibrary.response.HomeResponse;
import com.zhanlibrary.utils.GHSLogUtil;
import com.zhanlibrary.utils.ScreenUtils;
import com.zhanlibrary.widget.MyViewPager;
import com.zhanlibrary.widget.PageIndicator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bookzhan on 2015/8/13.
 * 最后修改者: bookzhan  version 1.0
 * 说明:首页轮播View,在Activity中的对应方法中调用onResume,onPause
 * 嵌套在Fragment中的话就调用setUserVisibleHint
 */
public class HomeCarouselImage extends HomeBaseView implements MyViewPager.MyTouchListener {
    private final int NEXT_PAGE = 78;
    private List<HomeBasesData> carousel_data = new ArrayList<>();
    private long delayMillis = 5000;
    private MyViewPager carousel_viewPager;
    private PageIndicator mPageIndicator;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case NEXT_PAGE:
                    handler.removeMessages(NEXT_PAGE);
                    if (!isInited) {
                        break;
                    }
                    carousel_viewPager.setCurrentItem(carousel_viewPager.getCurrentItem() + 1, true);
                    if (carousel_viewPager.getCurrentItem() + 10 > Integer.MAX_VALUE) {//保证极端情况下还能运行
                        carousel_viewPager.setCurrentItem(carousel_viewPager.getCurrentItem() - 20 * carousel_data.size());//一次不能跳太多了
//                        carousel_viewPager.setCurrentItem(Integer.MAX_VALUE / 2 - Integer.MAX_VALUE / 2 % focusImages.size());这种会有问题,会卡死
                    }
                    handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
                    break;
            }
            return false;
        }
    });
    private String TAG = "HomeCarouselImage";
    private boolean isInited = false;//判断是否初始化完成
    private CarouselAdapter carouselAdapter;

    public HomeCarouselImage(Context context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.fragment_home_top_sales_header, null);
        carousel_viewPager = (MyViewPager) view.findViewById(R.id.view_pager);
//        carousel_viewPager.setPageTransformer(false, new MyPageTransformer());
        Point screenSize = ScreenUtils.getScreenSize(context);
        int height = 400 * screenSize.x / 750;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) carousel_viewPager.getLayoutParams();
        if (params == null) {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        } else {
            params.height = height;
        }
        carousel_viewPager.setLayoutParams(params);
        mPageIndicator = (PageIndicator) view.findViewById(R.id.page_indicator);
        carousel_viewPager.addOnPageChangeListener(mPageIndicator);
        carousel_viewPager.requestTouchEvent(this);
        try {
            Field field = ViewPager.class.getDeclaredField("mScroller");
            field.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(carousel_viewPager.getContext(),
                    new DecelerateInterpolator());
            field.set(carousel_viewPager, scroller);
            scroller.setmDuration(1000);
        } catch (Exception e) {
            GHSLogUtil.e(TAG, e.toString());
        }

        return view;
    }

    public HomeCarouselImage initData() {
        loadFocusData();
        return this;
    }

    @Override
    public void refresh(Object data) {
        initData();
    }

    //得到轮播图数据
    private void loadFocusData() {

        GHSHttpClient.getInstance().post(HomeResponse.class, Constants.Url.HOME_CAROUSEL, new GHSHttpHandler<HomeResponse>() {
            @Override
            public void onSuccess(HomeResponse response) {
                carousel_data = response.getData();
                if (null == carouselAdapter) {
                    carouselAdapter = new CarouselAdapter(context, carousel_data);
                    carousel_viewPager.setAdapter(carouselAdapter);
                    carousel_viewPager.setCurrentItem(20 * carousel_data.size());
                } else {
                    carouselAdapter.changeList(carousel_data);
                }
                mPageIndicator.setPageCount(carousel_data.size(), null);
                if (carousel_data.size() != 0) {
                    isInited = true;isInited = true;
                    handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
                }
            }

            @Override
            public void onFailure(String content) {
                if (carousel_data.size() != 0) {
                    handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
                }
            }
        });
    }

    public void onResume() {
        if (isInited && null != handler) {
            handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
        }
    }

    public void onPause() {
        if (isInited && null != handler) {
            handler.removeMessages(NEXT_PAGE);
        }
    }


    @Override
    public void onMyTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeMessages(NEXT_PAGE);
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
                break;
            case MotionEvent.ACTION_CANCEL:
                handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
                break;
        }
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isInited && null != handler) {
            if (isVisibleToUser) {
                handler.sendEmptyMessageDelayed(NEXT_PAGE, delayMillis);
            } else {
                handler.removeMessages(NEXT_PAGE);
            }
        }
    }
}
