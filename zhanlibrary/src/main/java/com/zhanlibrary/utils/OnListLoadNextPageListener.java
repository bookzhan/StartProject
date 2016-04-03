package com.zhanlibrary.utils;

import android.view.View;

/**
 * Created by R on 2016/3/30.
 */
public interface OnListLoadNextPageListener {
    /**
     * 开始加载下一页
     *
     * @param view 当前RecyclerView/ListView/GridView
     */
    public void onLoadNextPage(View view);
}
