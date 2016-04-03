package com.bookzhan.utils;

import android.content.Context;

import com.bookzhan.base.MyApplication;
import com.zhanlibrary.base.Constants;
import com.zhanlibrary.model.UserInfo;
import com.zhanlibrary.utils.AESHelper;
import com.zhanlibrary.utils.GHSStringUtil;
import com.zhanlibrary.utils.SpUtils;

import java.io.File;

/**
 * Created by bookzhan on 2015/8/9.
 * 最后修改者: bookzhan  version 1.0
 * 说明:获取用户信息
 */
public class UserInfoUtils {

    public static boolean isLogin(Context context) {
        return !GHSStringUtil.isEmpty((String) SpUtils.get(context, Constants.User.USER_ID_KEY, ""));
    }

    public static String getUserId(Context context) {
        String member_id = (String) SpUtils.get(context, Constants.User.USER_ID_KEY, "");
        if (member_id.startsWith("ghs")) {
            member_id = member_id.substring(3);
            return member_id;
        }
        if (GHSStringUtil.isEmpty(member_id)) return "";
        try {
            member_id = AESHelper.getInstance().decrypt(member_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return member_id;
    }

    public static void saveUserId(Context context, String userId) {
        try {
            userId = AESHelper.getInstance().encrypt(userId);
        } catch (Exception e) {
            userId = "ghs" + userId;
            e.printStackTrace();
        }
        SpUtils.put(context, Constants.User.USER_ID_KEY, userId);
    }

    public static String getUserMobile(Context context) {
        return (String) SpUtils.get(context, Constants.User.USER_MOBILE_KEY, "");
    }



    public static void clearUerInfo(Context context) {
        SpUtils.remove(context, Constants.User.USER_ID_KEY);
        MyApplication.user_id = "";
        SpUtils.remove(context, Constants.User.USER_MOBILE_KEY);
        SpUtils.remove(context, Constants.User.USER_GENDER_KEY);
        SpUtils.remove(context, Constants.User.USER_PWD_KEY);
        SpUtils.remove(context, Constants.User.USER_LEVEL_KEY);
        SpUtils.remove(context, Constants.User.USER_BIRTHDAY_KEY);
        File file = new File(context.getCacheDir() + Constants.User.USER_IMG_NAME_KEY);
        if (file.exists()) file.delete();
    }

    public static UserInfo getUerInfo(Context context) {
        UserInfo userInfo = new UserInfo();

        String userId = getUserId(context);
        userInfo.setMember_id(userId);
        MyApplication.user_id = userId;
        userInfo.setNick_name((String) SpUtils.get(context, Constants.User.USER_NAME_KEY, ""));
        userInfo.setMobile((String) SpUtils.get(context, Constants.User.USER_MOBILE_KEY, ""));
        userInfo.setGender((String) SpUtils.get(context, Constants.User.USER_GENDER_KEY, ""));

        userInfo.setBirthday((String) SpUtils.get(context, Constants.User.USER_BIRTHDAY_KEY, "1970-01-01"));
        userInfo.setLevel((String) SpUtils.get(context, Constants.User.USER_LEVEL_KEY, ""));
        return userInfo;
    }



    /**
     * @param context
     * @param userInfo 保存用户信息,分开存取的时候方便一点
     */
    public static void saveUserInfo(Context context, UserInfo userInfo) {
        if (null == userInfo) return;
        SpUtils.put(context, Constants.User.USER_NAME_KEY, userInfo.getUname());
        SpUtils.put(context, Constants.User.USER_PWD_KEY, userInfo.getPwd());
        if (!GHSStringUtil.isEmpty(userInfo.getMember_id())) {
            saveUserId(context, userInfo.getMember_id());
            MyApplication.user_id = userInfo.getMember_id();
        }
        if (!GHSStringUtil.isEmpty(userInfo.getNick_name())) {
            SpUtils.put(context, Constants.User.USER_NICK_NAME_KEY, userInfo.getNick_name());
        } else {
            if (!GHSStringUtil.isEmpty(userInfo.getMobile())) {
                SpUtils.put(context, Constants.User.USER_MOBILE_KEY, userInfo.getMobile());
            }
        }
        if (!GHSStringUtil.isEmpty(userInfo.getMobile())) {
            SpUtils.put(context, Constants.User.USER_MOBILE_KEY, userInfo.getMobile());
        }
        if (!GHSStringUtil.isEmpty(userInfo.getGender())) {
            SpUtils.put(context, Constants.User.USER_GENDER_KEY, userInfo.getGender());
        }

        if (userInfo.getBirthday() != null) {
            SpUtils.put(context, Constants.User.USER_BIRTHDAY_KEY, userInfo.getBirthday());
        }

        if (!GHSStringUtil.isEmpty(userInfo.getLevel())) {
            SpUtils.put(context, Constants.User.USER_LEVEL_KEY, userInfo.getLevel());
        }

    }

    public static void login(Context context) {
//        Intent intent = new Intent(context, LoginActivity.class); //TODO LoginActivity
//        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//        context.startActivity(intent);
    }
}
