package com.zhanlibrary.utils;

import java.text.DecimalFormat;

/**
 * Created by youzi on 2015/10/23.
 */
public class NumericUtils {
    private static DecimalFormat df = new DecimalFormat("########.00");
    private static DecimalFormat df1 = new DecimalFormat("########");
    //保留 2位小数！！！！
    public static  String retain(double c) {
        if(c<=0)return "0.00";

        return df.format(c);
    }
    public static  String retain(String c) {
        try {
            double a = Double.valueOf(c);
           if (a <= 0) return "0.00";
           return df.format(a);
        }catch (Exception e){
            return "0.00";
        }
    }


    //保留整数
    public static String retainInteger(String c){
        try {
            double a = Double.valueOf(c);
            if (a <= 0) return "0";
            return df1.format(a);
        }catch (Exception e){
            return "0";
        }
    }
    public static  String retainInteger(double c) {
        if(c<=0)return "0";

        return df1.format(c);
    }
}
