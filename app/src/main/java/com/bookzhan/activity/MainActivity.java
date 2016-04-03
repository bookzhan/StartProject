package com.bookzhan.activity;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;

import com.bookzhan.adapter.HomePageAdapter;
import com.bookzhan.video.R;
import com.umeng.analytics.MobclickAgent;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.utils.GHSLogUtil;
import com.zhanlibrary.widget.CommonNavigation;
import com.zhanlibrary.widget.MyTabHost;
import com.zhanlibrary.widget.UnScrollViewPager;

public class MainActivity extends BaseActivity {

    private MyTabHost mTabHost;
    private int currentTabIndex;
    private final static String TAG = "MainActivity";
    private final long exitTime = 2000;  //2秒
    private long lastPressTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }


    private void initView() {
        final CommonNavigation navigation = (CommonNavigation) findViewById(R.id.navigation);
        navigation.hideLeftLayout();
        mTabHost = (MyTabHost) findViewById(R.id.my_tab_host);
        UnScrollViewPager contentViewPager = (UnScrollViewPager) findViewById(R.id.viewPager);
        mTabHost.setViewPager(contentViewPager);
        final HomePageAdapter homePageAdapter = new HomePageAdapter(getSupportFragmentManager());
        contentViewPager.setAdapter(homePageAdapter);

        mTabHost.setOnTabChangedListener(new MyTabHost.OnTabChangedListener() {
            @Override
            public void OnTabChanged(int tabIndex) {
                currentTabIndex = tabIndex;
                GHSLogUtil.d(TAG, "tabIndex=" + tabIndex);
                navigation.setTitle((String) homePageAdapter.getPageTitle(tabIndex));
            }
        });

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            if (SystemClock.elapsedRealtime() - lastPressTime > exitTime) {
                showToastAtBottom("再按一次退出程序");
            } else {
                MobclickAgent.onKillProcess(this);
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            lastPressTime = SystemClock.elapsedRealtime();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
