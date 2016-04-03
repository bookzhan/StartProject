package com.zhanlibrary.http;


import com.zhanlibrary.base.Constants;

import java.util.TreeMap;

/**
 * Created by bookzhan on 2015/8/7.
 * 说明:请求参数的封装
 */
public class GHSRequestParams {
    private TreeMap<String, String> paramMap = new TreeMap<>();

    public GHSRequestParams() {
        addParams("device_type", "android");
        addParams("member_id", Constants.USER_ID);
        addParams("version", Constants.VERSION);
    }

    public void addParams(String key, String param) {
        if (key == null || param == null) {
            return;
        }
        paramMap.put(key, param);
    }

    public TreeMap<String, String> getParamMap() {
        return paramMap;
    }
}
