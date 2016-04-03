package com.zhanlibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.zhanlibrary.base.BaseActivity;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.http.GHSHttpClient;
import com.zhanlibrary.http.GHSHttpHandler;
import com.zhanlibrary.http.GHSRequestParams;
import com.zhanlibrary.model.HomeBasesData;
import com.zhanlibrary.model.UpdateModel;
import com.zhanlibrary.response.UpdateResponse;
import com.zhanlibrary.widget.CommonDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * @Author bookzhan
 * @Date 2015-7-01
 * @Desc 一些比较通用的工具类
 */
public class CommonUtils {

    public static String getCurrentTime(String format) {
        return new SimpleDateFormat(format, Locale.getDefault()).format(new Date());
    }

    public static String getCurrentTime() {
        return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
    }


    /**
     * 是否有网络连接
     *
     * @return
     */
    public static boolean hasInternet(Context context) {
        try {
            if (context != null) {
                ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (mNetworkInfo != null) {
                    return mNetworkInfo.isAvailable();
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 获取版本更新的信息,直接调用这个就好了
     *
     * @param context
     * @param isComeMainActivity 是否是来自首页创建的时候
     */

    public static void checkUpdate(final Context context, final boolean isComeMainActivity) {
        final BaseActivity baseActivity = (BaseActivity) context;
        GHSRequestParams params = new GHSRequestParams();
        params.addParams("version", getVersion(context));
        if (!isComeMainActivity) {
            baseActivity.showLoading();
        }
        GHSHttpClient.getInstance().post4NoAllToast(UpdateResponse.class, Constants.Url.UPDATE, params, new GHSHttpHandler<UpdateResponse>() {
            @Override
            public void onFailure(String content) {
                if (!isComeMainActivity) {
                    baseActivity.hiddenLoadingView();
                    baseActivity.showToastAtCenter(null);
                }
            }

            @Override
            public void onSuccess(UpdateResponse response) {
                if (!isComeMainActivity) {
                    baseActivity.hiddenLoadingView();
                }
                if (null != response && null != response.getData()) {
                    setUpdateData(context, response.getData(), isComeMainActivity);
                } else if (!isComeMainActivity) {
                    baseActivity.showToastAtCenter(null);
                }

            }
        });


    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    /**
     * @param currentVersion 当前系统的版本
     * @param judgeVersion   需要判断的版本
     * @return 当前手机中的版本是不是最新的, 出错也会判断为最新版本
     */
    public static boolean isNewVersion(String currentVersion, String judgeVersion) {
        try {
            currentVersion = currentVersion.replace(".", "");
            judgeVersion = judgeVersion.replace(".", "");

            int systemVersion = Integer.parseInt(currentVersion);
            int tempVersion = Integer.parseInt(judgeVersion);

            if (systemVersion > 0 && tempVersion > 0) {
                return tempVersion <= systemVersion;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    public static void setUpdateData(final Context context, final UpdateModel update, final boolean isComeMainActivity) {
        final CommonDialog commonDialog = new CommonDialog(context);
        commonDialog.setLeftButtonMsg("知道了");
        commonDialog.setRightButtonMsg("稍候再说");
        if (isNewVersion(getVersion(context), update.getVersion())) {//版本号是最新的
            if (isComeMainActivity) {//来自首页创建的时候不显示对话框
                return;
            }
            commonDialog.setContentMsg("恭喜您，已经是最新版本");
            commonDialog.hideRightButton();
            commonDialog.setCanceledOnTouchOutside(true);
            commonDialog.show();
        } else {
            commonDialog.setRightButtonMsg("立即更新");
            if (!GHSStringUtil.isEmpty(update.getSummary())) {
                commonDialog.setContentMsg(update.getSummary());
            } else {
                commonDialog.setContentMsg("检测到最新版本，是否更新？");
            }
            if (1 == update.getForce_update()) {//强制更新
                commonDialog.setAutoDismiss(false);
                commonDialog.setCanceledOnTouchOutside(false);
                commonDialog.setCancelable(false);
                commonDialog.setLeftButtonMsg("立即更新");
                commonDialog.hideRightButton();
                commonDialog.setOnLeftButtonOnClickListener(new CommonDialog.LeftButtonOnClickListener() {
                    @Override
                    public void onLeftButtonOnClick() {
                        Uri uri = Uri.parse(update.getDownload_url());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
            } else {
                commonDialog.setCancelable(true);
                commonDialog.setCanceledOnTouchOutside(true);
                commonDialog.showRightButton();
                commonDialog.setOnRightButtonOnClickListener(new CommonDialog.RightButtonOnClickListener() {
                    @Override
                    public void onRightButtonOnClick() {
                        Uri uri = Uri.parse(update.getDownload_url());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        context.startActivity(intent);
                    }
                });
            }
            commonDialog.show();
        }
    }


    /**
     * @param price 格式化数字
     * @return
     */
    public static String formatPrice(double price) {
        return String.format("%.2f", price);
    }

    /**
     * @param number  原始数据
     * @param holdNum 要保留的小数小数
     * @return
     */
    public static String formatNumber(double number, int holdNum) {
        return String.format("%." + holdNum + "f", number);
    }

    /**
     * 计算抵扣
     *
     * @param price 传小数进来就好了
     * @return
     */
    public static String getDiscount(double price) {
        String temp = String.format("%.1f", price *= 10);
        if (temp.endsWith("0")) {
            return Float.valueOf(temp) > 10 ? "10" : temp.substring(0, 1);
        }
        return Float.valueOf(temp) > 10 ? "10" : temp;
    }


    /**
     * 拨打电话
     */
    public static void dialServicePhone(final Context context) {
//        CommonDialog commonDialog = new CommonDialog(context);
//        commonDialog.setContentMsg("现在就拨打客服电话?\n" + Constants.SERVICE_PHONE_NUM);
//        commonDialog.setOnRightButtonOnClick(new CommonDialog.RightButtonOnClickListener() {
//            @Override
//            public void onRightButtonOnClick() {
//                // 点击拨打跳转到拨号界面
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_CALL);
//                intent.setData(Uri.parse(Constants.TELPHONENUM));
//                context.startActivity(intent);
//            }
//        });
//        commonDialog.show();
    }

    /**
     * 发送短信
     *
     * @param phoneNumber
     * @param message
     */
    public static void sendMessage(Context context, String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("smsto", phoneNumber, null));
        intent.putExtra("sms_body", message);
        context.startActivity(Intent.createChooser(intent, null));
    }

    /**
     * 浏览网页
     *
     * @param webUrl
     */
    public static void viewUrl(Context context, String webUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.valueOf(webUrl)));
        context.startActivity(Intent.createChooser(intent, null));
    }


    /**
     * 检测网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();
        return !(networkinfo == null || !networkinfo.isAvailable());
    }

    /**
     * 检测wifi是否可用
     *
     * @param context
     * @return
     */
    public static boolean checkWifiAvailabl(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (NetworkInfo.State.CONNECTED == wifi) {
            return true;
        } else if (NetworkInfo.State.CONNECTED == mobile) {
            return false;
        }
        return false;
    }


    /**
     * 打印
     *
     * @param
     * @return
     */
    public static String prettyPrint(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }

    public static void clearCache(Context context) {
        File directory = context.getCacheDir();
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }


    /**
     * @param startTime 单位为秒
     * @param end_time  单位为秒
     * @return 精确到小时, 分开返回
     */
    public static String[] getTime(long startTime, long end_time) {
        long second = end_time - startTime;
        if(second<0)second=0;

        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String[] time = new String[3];
        if (0 != hh) {
            time[0] = String.format("%02d", hh);
            time[1] = String.format("%02d", mm);
            time[2] = String.format("%02d", ss);
        } else {
            time[0] = "00";
            time[1] = String.format("%02d", mm);
            time[2] = String.format("%02d", ss);
        }
        return time;
    }


    /**
     * @param second 单位为秒
     * @return 返回格式为 23:21
     */
    public static String getTime(long second) {
        if (second < 0) {
            return "00:00";
        }
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        long ss = second % 60;
        String strTemp;
        if (0 != hh) {
            strTemp = String.format("%02d:%02d:%02d", hh, mm, ss);
        } else {
            strTemp = String.format("%02d:%02d", mm, ss);
        }
        return strTemp;
    }

    /**
     * @param second 单位为秒
     * @return 返回格式为 23:21
     */
    public static String getTimes(long second) {
        if (second < 0) {
            return "00:00";
        }
        long hh = second / 3600;
        long mm = second % 3600 / 60;
        return String.format("%02d:%02d", hh, mm);
    }

    /**
     * 大于一天的截取掉
     *
     * @param second 单位为秒
     * @return 返回格式为 23:21
     */
    public static String getTime4HH(long second) {
        if (second < 0) {
            return "00:00";
        }
        Date date = new Date(second * 1000);
        int hours = date.getHours();
        int minutes = date.getMinutes();
        return String.format("%02d:%02d", hours, minutes);
    }

    public static void judgeWhereToGo(Context context, HomeBasesData focusImage) {

    }


//
//    public static void showShare(final BaseActivity activity, final ShareData data) {
//        IWXAPI api = WXAPIFactory.createWXAPI(activity, null);
//        if (!api.isWXAppInstalled()) {
//            activity.showDialogMsg("没有安装微信客户端");
//            return;
//        }
//
//        View view = View.inflate(activity, R.layout.share_view, null);
//        final MyDialog myDialog = new MyDialog(activity, view, Gravity.BOTTOM);
//
//        view.findViewById(R.id.share_wechat).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                share(activity,data, "Wechat");
//                myDialog.dismiss();
//            }
//        });
//        view.findViewById(R.id.share_wechat_moments).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                share(activity,data, "WechatMoments");
//                myDialog.dismiss();
//            }
//        });
//        view.findViewById(R.id.share_sina).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                share(activity,data, "SinaWeibo");
//                myDialog.dismiss();
//            }
//        });
//        view.findViewById(R.id.share_canel).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myDialog.dismiss();
//            }
//        });
//        myDialog.show();
//    }
//
//    private static void share(final BaseActivity activity, ShareData datas, String shareTo) {
//        activity.showLoading();
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("platform", shareTo);
//        data.put("text", datas.getText());
//        data.put("title", datas.getTitle());
//        data.put("imageUrl", datas.getImgUrl());
//        data.put("url", datas.getUrl());
//
//        HashMap<Platform, HashMap<String, Object>> shareData = new HashMap<>();
//        shareData.put(ShareSDK.getPlatform(shareTo), data);
//        ShareSDK.setConnTimeout(10000);
//        ShareSDK.setReadTimeout(10000);
//        OnekeyShare oks = new OnekeyShare();
//        oks.setCallback(new PlatformActionListener() {
//            @Override
//            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
//                activity.hiddenLoadingView();
//                activity.showDialogMsg("分享成功");
//            }
//
//            @Override
//            public void onError(Platform platform, int i, Throwable throwable) {
//                activity.hiddenLoadingView();
//                activity.showDialogMsg("分享失败");
//            }
//
//            @Override
//            public void onCancel(Platform platform, int i) {
//                activity. hiddenLoadingView();
//            }
//        });
//        oks.share(shareData);
//
//    }


}
