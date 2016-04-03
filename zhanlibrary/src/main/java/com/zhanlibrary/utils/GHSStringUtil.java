package com.zhanlibrary.utils;

import java.security.MessageDigest;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class GHSStringUtil {

    /**
     * 产生一个随机的字符串
     *
     * @param length 字符串长度
     * @return
     */
    public static String randomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 严格判断字符串是否有值，如果为null或者是空字符串或者只有空格，则返回true，否则则返回false
     *
     * @param value
     * @return
     */
    public static boolean isEmpty(String value) {
        return value == null || "".equalsIgnoreCase(value.trim()) || "null".equalsIgnoreCase(value.trim());
    }


    //ref: http://www.mkyong.com/regular-expressions/how-to-validate-email-address-with-regular-expression/
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    //ref: http://www.w3.org/TR/html-markup/datatypes.html#form.data.emailaddress
//    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:.[a-zA-Z0-9-]+)*$";

    /**
     * 判断是否合法的Email地址
     *
     * @param target
     * @return
     */
    public final static boolean isValidEmail(String target) {
        if (isEmpty(target)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(target);
            return matcher.matches();
        }
    }

    /**
     * 取得二进制数据的16进制显示字符串
     */
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    /**
     * 判断给定的字符串是不是数字
     *
     * @param strNum
     * @return
     */
    public static boolean isNumber(String strNum) {
        boolean result = false;
        if (strNum == null || "".equalsIgnoreCase(strNum)) {
            result = false;
        } else {
            Pattern pattern = Pattern.compile("^[0-9]*$");
            Matcher matcher = pattern.matcher(strNum);
            result = matcher.matches();
        }

        return result;
    }

    /**
     * 将字符串转成MD5值
     *
     * @param string
     * @return
     */
    public static String getMD5(String string) {
        if (GHSStringUtil.isEmpty(string)) {
            return "";
        }
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    /**
     * 二行制转字符串
     */
    public static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer();
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs.append("0").append(stmp);
            else
                hs.append(stmp);
        }

        return hs.toString().toUpperCase();
    }

}
