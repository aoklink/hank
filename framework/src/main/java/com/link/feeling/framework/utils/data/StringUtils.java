package com.link.feeling.framework.utils.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created on 2019/1/7  10:22
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class StringUtils {


    private StringUtils() {
        throw new UnsupportedOperationException("工具类不能调用构造函数");
    }

    /**
     * 为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return s == null || s.length() == 0 || s.equals("null");
    }

    /**
     * 不为空
     *
     * @param s
     * @return
     */
    public static boolean isNotEmpty(String s) {
        return !isEmpty(s);
    }

    /**
     * 参数为空时返回“”,否则返回参数本身
     *
     * @param s
     * @return
     */
    public static String getString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 手机号
     *
     * @param mobile
     * @return
     */
    public static boolean isMobile(String mobile) {
//		String str = "^((1(3[0-9]|5[0-35-9]|8[0-9])\\d{8})|(0(10|2[0-5789]|\\d{3})\\d{7,8}))$";
        String str = "^((1\\d{10})|(0(10|2[0-5789]|\\d{3})\\d{7,8}))$";// 手机号
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(mobile);
        return m.matches();
    }

}
