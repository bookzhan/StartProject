package com.zhanlibrary.http;

public interface GHSHttpResponseHandler {
    void onSuccess(String content);

    void onFailure(String content);
}