package com.zhanlibrary.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import com.google.gson.Gson;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.zhanlibrary.R;
import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.utils.AESHelper;
import com.zhanlibrary.utils.GHSLogUtil;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.RSAUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhandalin on 2015-10-28 16:04.
 * 说明:基于OkHttpClient与自身的逻辑进行了简单封装
 */
public class GHSHttpClient {
    private OkHttpClient okHttpClient;

    private static GHSHttpClient ghsHttpClient;
    private static final String TAG = "GHSHttpClient";
    private final Handler uiHandler;
    private AESHelper aesHelper;

    private GHSHttpClient() {
        okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(50, TimeUnit.SECONDS);
        okHttpClient.setReadTimeout(50, TimeUnit.SECONDS);
        uiHandler = new Handler(Looper.getMainLooper());
    }

    public static GHSHttpClient getInstance() {
        if (null == ghsHttpClient) {
            synchronized (GHSHttpClient.class) {
                if (null == ghsHttpClient) {
                    ghsHttpClient = new GHSHttpClient();
                }
            }
        }
        return ghsHttpClient;
    }


    /**
     * post请求,错误的时候会弹Toast,status为false的时候会弹message所包含的信息
     * 会在子线程中解析json
     */
    public <T> void post(Class<T> object, Context context, String url, GHSRequestParams params, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, (BaseActivity) context, transformInfo4Post(params, url), responseHandler, false);
    }


    /**
     * 不带参数的post请求,错误的时候不弹Toast,status为false的时候会弹message所包含的信息
     * 会在子线程中解析json
     */
    public <T> void post(Class<T> object, String url, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, null, transformInfo4Post(new GHSRequestParams(), url), responseHandler, false);
    }

    /**
     * 不带参数的post请求,错误的时候会弹Toast,status为false的时候会弹message所包含的信息
     * 会在子线程中解析json
     */
    public <T> void post(Class<T> object, Context context, String url, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, (BaseActivity) context, transformInfo4Post(new GHSRequestParams(), url), responseHandler, false);
    }

    /**
     * 带参数的post请求,错误的时候会弹Toast,status为false的时候会弹message所包含的信息
     * 会在子线程中解析json
     */
    public <T> void post(Class<T> object, String url, GHSRequestParams params, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, null, transformInfo4Post(params, url), responseHandler, false);
    }

    /**
     * 不弹任何信息,返回的数据根元素是data
     */
    @Deprecated
    public void post(String url, GHSRequestParams params, GHSHttpResponseHandler responseHandler) {
        sendPostRequest(null, null, transformInfo4Post(params, url), responseHandler, false);
    }

    /**
     * 不带参数
     * 这个只会弹status为false的时候,message所包含的信息
     * 适合调用者自定义错误信息
     * 会在子线程中解析json
     */
    public <T> void post4NoErrorToast(Class<T> object, Context context, String url, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, (BaseActivity) context, transformInfo4Post(new GHSRequestParams(), url), responseHandler, true);
    }


    /**
     * 这个只会弹status为false的时候,message所包含的信息
     * 适合调用者自定义错误信息
     * 会在子线程中解析json
     */
    public <T> void post4NoErrorToast(Class<T> object, Context context, String url, GHSRequestParams params, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, (BaseActivity) context, transformInfo4Post(params, url), responseHandler, true);
    }


    /**
     * 带参数的post请求,错误的时候不弹任何Toast
     * 会在子线程中解析json
     */
    public <T> void post4NoAllToast(Class<T> object, String url, GHSRequestParams params, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, null, transformInfo4Post(params, url), responseHandler, true);
    }


    /**
     * 不带参数的post请求,错误的时候不弹任何Toast
     * 会在子线程中解析json
     */
    public <T> void post4NoAllToast(Class<T> object, final String url, GHSHttpHandler<T> responseHandler) {
        sendPostRequest(object, null, transformInfo4Post(new GHSRequestParams(), url), responseHandler, true);
    }

    /**
     * 直接返回网络请求的数据不做任何预处理,如果预处理的话会把有些转义字符处理掉
     * 有相应需求直接调用这个方法
     */
    public void post4NoParseJson(final String url, GHSRequestParams params, GHSHttpResponseHandler responseHandler) {
        sendPostRequest(transformInfo4Post(params, url), responseHandler);
    }

    /**
     * 异步下载文件
     *
     * @param targetFile 全路径
     */
    public void downloadAsyn(final String url, final String targetFile, final GHSHttpResponseHandler responseHandler) {
        try {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            final Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    callFail(e.toString(), responseHandler, null, true);
                }

                @Override
                public void onResponse(Response response) {
                    InputStream is = null;
                    byte[] buf = new byte[2048];
                    int len;
                    FileOutputStream fos = null;
                    try {
                        is = response.body().byteStream();
                        File file = new File(targetFile);
                        fos = new FileOutputStream(file);
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                        }
                        fos.flush();
                        callSuccess(null, targetFile + "下载成功", responseHandler, null);
                    } catch (IOException e) {
                        callFail(e.toString(), responseHandler, null, true);
                    } finally {
                        try {
                            if (is != null) is.close();
                        } catch (IOException e) {
                        }
                        try {
                            if (fos != null) fos.close();
                        } catch (IOException e) {
                        }
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步上传文件,不弹消息
     *
     * @param targetFile 全路径名字
     */
    public void uploadAsyn4NoTost(final String url, GHSRequestParams params, final String targetFile, final GHSHttpResponseHandler responseHandler) {

        params.addParams("method", url);
        params.addParams("sign", getSign(params, Constants.TOKEN));

        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);

        for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
            multipartBuilder.addFormDataPart(temp.getKey(), null, RequestBody.create(null, temp.getValue()));
        }

        File file = new File(targetFile);
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
        multipartBuilder.addFormDataPart("file", file.getName(), fileBody);

        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .post(multipartBuilder.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callFail(e.toString(), responseHandler, null, false);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                callSuccess(null, response.body().string(), responseHandler, null);
            }
        });
    }

    /**
     * 异步上传文件
     *
     * @param destFile 全路径名字
     */
    public void uploadAsyn(Context context, final String url, GHSRequestParams params, final String destFile, final GHSHttpResponseHandler responseHandler) {
        final BaseActivity baseActivity = (BaseActivity) context;
        params.addParams("method", url);
        params.addParams("sign", getSign(params, Constants.TOKEN));

        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);

        for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
            multipartBuilder.addFormDataPart(temp.getKey(), null, RequestBody.create(null, temp.getValue()));
        }

        if (!GHSStringUtil.isEmpty(destFile)) {
            File file = new File(destFile);
            if (file.isFile()) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                multipartBuilder.addFormDataPart("file", file.getName(), fileBody);
            }
        }

        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .post(multipartBuilder.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callFail(e.toString(), responseHandler, baseActivity, false);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                callSuccess(null, response.body().string(), responseHandler, baseActivity);
            }
        });
    }

    /**
     * 异步上传文件
     *
     * @param destFiles 图片地址数组
     */
    public void multiUploadAsyn(Context context, final String url, GHSRequestParams params, final List<String> destFiles, final GHSHttpResponseHandler responseHandler) {
        final BaseActivity baseActivity = (BaseActivity) context;
        params.addParams("method", url);
        params.addParams("sign", getSign(params, Constants.TOKEN));

        MultipartBuilder multipartBuilder = new MultipartBuilder();
        multipartBuilder.type(MultipartBuilder.FORM);

        for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
            multipartBuilder.addFormDataPart(temp.getKey(), null, RequestBody.create(null, temp.getValue()));
        }

        if (destFiles != null && destFiles.size() > 0) {
            for (int i = 0; i < destFiles.size(); i++) {
                File file = new File(destFiles.get(i));
                if (file.isFile()) {
                    RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                    multipartBuilder.addFormDataPart("file" + i + 1, file.getName(), fileBody);
                }
            }
        }

        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .post(multipartBuilder.build())
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                callFail(e.toString(), responseHandler, baseActivity, false);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                callSuccess(null, response.body().string(), responseHandler, baseActivity);
            }
        });
    }


    /**
     * 不建议使用,如果要使用的话会导致环球的接口调不通
     * <p/>
     * 直接请求服务器,不会做任何预处理,返回结果也不会做任何处理
     *
     * @param url 请求的绝对地址
     */
    public void requestService(final String url, final GHSHttpResponseHandler responseHandler) {
        try {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            final Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(final Request request, final IOException e) {
                    callFail(e.toString(), responseHandler, null, true);
                }

                @Override
                public void onResponse(Response response) {
                    try {
                        callSuccess(null, response.body().string(), responseHandler, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Request transformInfo4Post(GHSRequestParams params, String method) {
        try {
            GHSLogUtil.d(TAG, getRequestUrl(params, method));
            switch (method) {
                default:// TODO: 16/4/3 选择加密的接口
//                case Constants.Url.PERSONAL_ACCOUNT_QUERY:
//                case Constants.Url.ORDER_SUBMIT_API:
//                case Constants.Url.SEND_CODE_API:
//                case Constants.Url.REGISTER_API:
//                case Constants.Url.PATH_LOGIN:
//                case Constants.Url.MODIFY_PASSWORD:
//                case Constants.Url.RESET_PWD:
//                case Constants.Url.ORDERDETAIL_API:
//                case Constants.Url.ORDERLIST_API:
//                case Constants.Url.CANCEL_ORDER:
                    String json = new Gson().toJson(params.getParamMap());
                    if (json.getBytes().length < 110) {
                        return RSAEncrypt(params, json, method);
                    } else {
                        return AESEncrypt(params, json, method);
                    }

            }
        } catch (Exception e) {
            GHSLogUtil.d(TAG, e.toString());
        }
        params.addParams("method", method);
        return getRequest(params);
    }

    public String getRequestUrl(GHSRequestParams params, String method) {
        String url = null;
        if (Constants.DEVELOPER_MODE) {
            url = Constants.SERVER_URL + "?method=" + method + "&";
            for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
                url += temp.getKey() + "=" + temp.getValue() + "&";
            }
            url += "sign=" + getSign(params, Constants.TOKEN);
        }
        return url;
    }

    /**
     * 这个会预处理json
     */
    private <T> void sendPostRequest(final Class<T> object, final BaseActivity baseActivity, Request request, final GHSHttpResponseHandler responseHandler, final boolean noErrorToast) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                callFail(e.toString(), responseHandler, baseActivity, noErrorToast);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    parseJson(object, response.body().string(), responseHandler, baseActivity, noErrorToast);
                } catch (final Exception e) {
                    GHSLogUtil.d(TAG, e.toString());
                    callFail(e.toString(), responseHandler, baseActivity, noErrorToast);
                }
            }
        });
    }


    /**
     * 这个方法不会预处理json,直接返回服务器结果
     */
    private void sendPostRequest(Request request, final GHSHttpResponseHandler responseHandler) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                callFail(e.toString(), responseHandler, null, true);
            }

            @Override
            public void onResponse(final Response response) {
                try {
                    callSuccess(null, response.body().string(), responseHandler, null);
                } catch (final Exception e) {
                    GHSLogUtil.d(TAG, e.toString());
                    callFail(e.toString(), responseHandler, null, true);
                }
            }
        });
    }

    private String getSign(GHSRequestParams params, String token) {
        return GHSStringUtil.getMD5(GHSStringUtil.getMD5(assemble(params)).toUpperCase() + token).toUpperCase();
    }

    private String assemble(GHSRequestParams params) {
        String sign = "";
        for (String key : params.getParamMap().keySet()) {//这个map是treemap默认就是自然排序
            sign += key + params.getParamMap().get(key);
        }
        return sign;
    }


    /**
     * 初步的解析出json,来判定是否应该显示来自服务端的消息
     *
     * @throws JSONException
     */
    private <T> void parseJson(Class<T> object, String content, GHSHttpResponseHandler responseHandler, BaseActivity baseActivity, boolean noErrorToast) throws Exception {
        GHSLogUtil.d(TAG, "result=" + content);
        JSONObject jsonObject = new JSONObject(content);
        String errorMsg = "网络请求失败~";
        if (jsonObject.has("rsp") && "fail".equals(jsonObject.getString("rsp"))) {
            callFail(errorMsg, responseHandler, baseActivity, noErrorToast);
            return;
        }
        Object tempData = jsonObject.get("data");
        if (null == tempData) {
            callSuccess(object, "data数据为空", responseHandler, baseActivity);
            return;
        }
        if (tempData instanceof JSONObject) {
            JSONObject data = (JSONObject) tempData;
            String returndata = null;
            if (data.has("returndata")) {
                returndata = data.getString("returndata");
                if (GHSStringUtil.isEmpty(returndata)) {
                    returndata = "{\"flag\":\"jsonError\"}";
                } else {
                    returndata = "{\"data\":" + returndata + "}";
                }
            }
            if (data.has("status")) {
                if (data.getBoolean("status")) {
                    callSuccess(object, returndata, responseHandler, baseActivity);
                } else {
                    if (data.has("message")) {
                        if (null != baseActivity)
                            showToast(baseActivity, data.getString("message"));
                    } else {
                        if (null != baseActivity)
                            showToast(baseActivity, data.getString(errorMsg));
                    }
                }
            } else {
                callSuccess(object, returndata, responseHandler, baseActivity);
            }
        } else {
            if (null != baseActivity)
                showToast(baseActivity, errorMsg);
            callFail(errorMsg, responseHandler, baseActivity, noErrorToast);
        }
    }


    private <T> void callSuccess(final Class<T> object, final String content, final GHSHttpResponseHandler responseHandler, final BaseActivity baseActivity) {
        T response = null;
        try {
            if (responseHandler instanceof GHSHttpHandler && null != object) {
                response = new Gson().fromJson(content, object);
            }
        } catch (Exception e) {
            callFail("json解析失败", responseHandler, baseActivity, false);
            e.printStackTrace();
            return;
        }
        final T finalResponse = response;
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != baseActivity) {
                        baseActivity.hiddenLoadingView();
                        baseActivity.setContentView(baseActivity.rootView);
                    }
                    if (null != finalResponse || null != object) {
                        ((GHSHttpHandler) responseHandler).onSuccess(finalResponse);
                    } else
                        responseHandler.onSuccess(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 当网络错误,或者json格式错误就会调这个方法
     *
     * @param noErrorToast true 的时候网络错误不弹错误信息
     */

    private void callFail(final String content, final GHSHttpResponseHandler responseHandler, final BaseActivity baseActivity, final boolean noErrorToast) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (null != baseActivity) {
                        baseActivity.hiddenLoadingView();
                        if (baseActivity.errorView != null) {
                            baseActivity.errorView.findViewById(R.id.error_naviagation).setVisibility(View.VISIBLE);
                            baseActivity.errorView.setVisibility(View.VISIBLE);
                            baseActivity.setContentView(baseActivity.errorView);
                        } else if (!noErrorToast) {
                            baseActivity.showToastAtCenter(null);
                        }
                    }
                    responseHandler.onFailure(content);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void showToast(final BaseActivity baseActivity, final String content) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null != baseActivity) {
                    baseActivity.hiddenLoadingView();
                    baseActivity.showToastAtCenter(content);
//                    if ("您长期未登录，请重新登录或注册".equals(content)) {
//                        UserInfoUtils.clearUerInfo(baseActivity);
//                        Intent intent = new Intent(baseActivity, LoginActivity.class);
//                        intent.putExtra("needReturnMainActivity", false);
//                        baseActivity.startActivity(intent);
//                    } else if ("此商品不存在".equals(content)) {
//                        uiHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                baseActivity.finish();
//                                baseActivity.overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
//                            }
//                        }, 1000);
//                    }
                }
            }
        });
    }

    private Request RSAEncrypt(GHSRequestParams params, String json, String method) {

        String encryptStr = RSAUtils.encryptByPublic(json, Constants.PUBLIC_KEY);
        if (null != encryptStr)
            encryptStr = encryptStr.replace("+", "$");
        params.getParamMap().clear();
        params.addParams("method", method);
        params.addParams("encodeurl", encryptStr);
        return getRequest(params);
    }

    private Request AESEncrypt(GHSRequestParams params, String json, String method) throws Exception {
        if (null == aesHelper)
            aesHelper = AESHelper.getInstance();
        String encryptStr = aesHelper.encrypt(json);
        if (null != encryptStr)
            encryptStr = encryptStr.replace("+", "$");
        params.getParamMap().clear();
        params.addParams("method", method);
        params.addParams("encodeurl", encryptStr);
        params.addParams("encount", "1");
        return getRequest(params);
    }

    private Request getRequest(GHSRequestParams params) {
        FormEncodingBuilder encodingBuilder = new FormEncodingBuilder();
        for (Map.Entry<String, String> temp : params.getParamMap().entrySet()) {
            encodingBuilder.add(temp.getKey(), temp.getValue());
        }
        encodingBuilder.add("sign", getSign(params, Constants.TOKEN));
        Request request = new Request.Builder()
                .url(Constants.SERVER_URL)
                .post(encodingBuilder.build()).build();

        return request;
    }
}
