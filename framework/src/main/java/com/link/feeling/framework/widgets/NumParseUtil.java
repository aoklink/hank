package com.link.feeling.framework.widgets;

import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Created on 2019/1/29  19:02
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public class NumParseUtil {
    public static long parseLong(String num) {
        long rNum = 0L;
        if (isNumber(num)) {
            rNum = Long.parseLong(num);
        }
        return rNum;
    }

    public static int parseInt(String num) {
        int rNum = 0;
        if (isInt(num)) {
            rNum = Integer.parseInt(num);
        }
        return rNum;
    }

    public static float parseFloat(String num) {
        float rNum = 0f;
        if (isNumber(num)) {
            rNum = Float.parseFloat(num);
        }
        return rNum;
    }

    public static double parseDouble(String num) {
        double rNum = 0d;
        if (isNumber(num)) {
            rNum = Double.parseDouble(num);
        }
        return rNum;
    }

    /**
     * 判断正负、整数小数
     *
     * @param str
     * @return
     */
    public static boolean isNumber(String str) {
        if (null == str) {
            return false;
        }
        return isInt(str) || isDouble(str);

    }

    public static boolean isInt(String str) {
        if (null == str) {
            return false;
        }
        return Pattern.compile("^-?[1-9]\\d*$").matcher(str).find();
    }

    public static boolean isDouble(String str) {
        if (null == str) {
            return false;
        }
        return Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$").matcher(str).find();
    }

    /**
     * 转化数字为Double的String，保留2为小数
     * @param value
     * @return
     */
    public static String parseDecimal(double value) {
        DecimalFormat df   = new DecimalFormat("######0.00");
        return df.format(value);
    }
}
