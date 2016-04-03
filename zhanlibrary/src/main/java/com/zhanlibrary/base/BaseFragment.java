package com.zhanlibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;


/**
 * Created by zhandalin on 2016-02-26 13:20.
 * 说明:
 */
public class BaseFragment extends Fragment {
    protected BaseActivity context;
    private boolean hasInited;
    protected boolean visible;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = (BaseActivity) getActivity();
    }

    @Override
    public void startActivity(Intent intent) {
        context.startActivity(intent);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (!hasInited) {
                hasInited = true;
                visible=true;
                initData();
            } else
                onMyResume();
        } else if (hasInited) {
            onMyPause();
        }
    }


    /**
     * 保证延迟加载,仅仅适用于首页,其它类要适用的话注意一下顺序
     */
    protected void initData() {
    }


    /**
     * 第一次不会调用这个方法,第一次会调用{@link #initData()},这样来保证数据加载一次
     * 当重新回到这个页面会调用这个方法
     */
    protected void onMyResume() {
    }

    /**
     * 页面失去焦点
     */
    protected void onMyPause() {
    }


}
