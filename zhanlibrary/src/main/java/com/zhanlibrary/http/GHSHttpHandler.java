package com.zhanlibrary.http;

public abstract class  GHSHttpHandler<T> implements GHSHttpResponseHandler {
    @Override
    public void onSuccess(String content) {

    }
    public abstract void onSuccess(T response);

}
