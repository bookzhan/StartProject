package com.zhanlibrary.view;

import android.content.Context;
import android.view.View;

/**
 * Created by bookzhan on 2015/8/13.
 * 最后修改者: bookzhan  version 1.0
 * 说明:基础View,这么设计的目的在于模块化代码,而且相对于其他View显得太轻量级了
 */
public abstract class HomeBaseView<T> {
    protected final View view;
    protected final Context context;

    public HomeBaseView(Context context) {
        this.context = context;
        view = initView();
    }

    /**
     * 让子类去初始化View
     *
     * @return
     */
    protected abstract View initView();

    public View getView() {
        return view;
    }

    /**
     * 让子类去初始化数据
     */
    protected abstract HomeBaseView initData();

    public abstract void refresh(T data);

    public abstract void onPause();

    public abstract void onResume();

}
