package com.link.feeling.framework.utils.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.link.feeling.framework.base.BaseApplication;
import com.link.feeling.framework.KeysConstants;

/**
 * Created on 2019/1/3  19:12
 * chenpan pan.chen@linkfeeling.cn
 * <p>
 * SharedPreferences
 */
@SuppressWarnings("all")
public final class SPUtils {

    private SPUtils() {
        throw new UnsupportedOperationException("工具类不能调用构造函数");
    }


    private static SharedPreferences getSharedPreferences() {
        return BaseApplication.getAppContext().getSharedPreferences(BaseApplication.getAppContext().getPackageName(),
                Context.MODE_PRIVATE);
    }

    public static void clear() {
        getSharedPreferences().edit().clear().apply();
    }

    public static void putLong(String key, long value) {
        getSharedPreferences().edit().putLong(key, value).apply();
    }

    public static long getLong(String key) {
        return getSharedPreferences().getLong(key, 0);
    }

    public static void putInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }

    public static int getInt(String key) {
        return getSharedPreferences().getInt(key, 0);
    }

    public static void putFloat(String key, float value) {
        getSharedPreferences().edit().putFloat(key, value).apply();
    }

    public static float getFloat(String key) {
        return getSharedPreferences().getFloat(key, 0);
    }

    public static void setBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(String key) {
        return getSharedPreferences().getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean value) {
        return getSharedPreferences().getBoolean(key, value);
    }

    public static void putBoolean(String key, boolean value) {
        getSharedPreferences().edit().putBoolean(key, value).apply();
    }

    public static Object getObject(String key) {
        return getSharedPreferences().getAll().get(key);
    }

    private static void putString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }

    public static String getString(String key) {
        return getSharedPreferences().getString(key, "");
    }

    public static String getString(String key, String value) {
        return getSharedPreferences().getString(key, value);
    }


    public static boolean isLogin() {
        return getBoolean(KeysConstants.LOGIN_STATUS , false);
    }

    public static boolean getLoginStatus() {
        return getBoolean(KeysConstants.LOGIN_STATUS);
    }

    public static void setLoginStatus(boolean value) {
        putBoolean(KeysConstants.LOGIN_STATUS, value);
    }

    public static String getUID() {
        return getString(KeysConstants.UID);
    }

    public static void setUID(String value) {
        putString(KeysConstants.UID, value);
    }

    public static String getSessionId() {
        return getString(KeysConstants.SESSION_ID);
    }

    public static void setSessionId(String value) {
        putString(KeysConstants.SESSION_ID, value);
    }

    public static String getUserType() {
//        return getString(KeysConstants.USER_TYPE);
        return "trainee";
    }

    public static void setUserType(String value) {
        putString(KeysConstants.USER_TYPE, value);
    }

    public static String getPhoneNum() {
        return getString(KeysConstants.PHONE_NUM);
    }

    public static void setPhoneNum(String value) {
        putString(KeysConstants.PHONE_NUM, value);
    }

    public static String getCode() {
        return getString(KeysConstants.SMS);
    }

    public static void setCode(String value) {
        putString(KeysConstants.SMS, value);
    }

    public static String getAvatar() {
        return getString(KeysConstants.AVATAR);
    }

    public static void setAvatar(String value) {
        putString(KeysConstants.AVATAR, value);
    }

    public static String getName() {
        return getString(KeysConstants.AVATAR);
    }

    public static void setName(String value) {
        putString(KeysConstants.AVATAR, value);
    }
}
