package com.zhanlibrary.response;

import com.zhanlibrary.model.HomeBasesData;

import java.util.List;

/**
 * Created by zhandalin 2015年09月01日 13:22.
 * 最后修改者: zhandalin  version 1.0
 * 说明:首页大多数的Response数据封装
 */
public class HomeResponse extends BaseResponse {
    private List<HomeBasesData> data;

    public List<HomeBasesData> getData() {
        return data;
    }

    public void setData(List<HomeBasesData> data) {
        this.data = data;
    }
}
