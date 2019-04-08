package com.link.feeling.framework.component.net.config;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.link.feeling.framework.component.net.NetResult;
import com.link.feeling.framework.component.net.exception.NetException;

import org.json.JSONObject;

/**
 * Created on 2019/1/17  11:41
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class HttpErrorHandler {
    // 请求成功
    private static final String ERROR_CODE_SUCCESS = "200";
    // 用户没有注册
    private static final String ERROR_CODE_REGISTER = "103";
    // 手机号已经注册
    private static final String ERROR_CODE_REGISTERED = "104";


    /**
     * 是否请求成功
     *
     * @param result 响应数据
     * @return true 为请求成功
     */
    public static boolean isSuccess(NetResult<?> result) {
        return result != null && result.isSuccess();
    }

    /**
     * 网络异常处理工具类
     *
     * @param exception     异常
     * @param errorCallback 错误回调
     */
    public static void handleException(@NonNull NetException exception, @Nullable ErrorCallback errorCallback) {
        String message = "系统错误";
        String code = exception.code();
        if (!TextUtils.isEmpty(exception.errorMsg())) {
            message = exception.errorMsg();
        }
        if (TextUtils.isEmpty(code)) {
            return;
        }
        switch (code) {
            //103==没有注册
            case ERROR_CODE_REGISTER:
                if (errorCallback != null) {
                    errorCallback.needRegister(message, code);
                }
                break;
            //103==没有注册
            case ERROR_CODE_REGISTERED:
                if (errorCallback != null) {
                    errorCallback.needLogin(message, code);
                }
                break;
            default:
                if (errorCallback != null) {
                    errorCallback.otherError(exception, code, message);
                }
                break;
        }
    }


    public abstract static class ErrorCallbackAdapter implements ErrorCallback {


        /**
         * 请求成功
         *
         * @param response 响应数据
         */
        @Override
        public void success(@NonNull JSONObject response) {

        }

        /**
         * 登录
         *
         * @param message 错误信息
         * @param code    错误码
         */
        @Override
        public void needLogin(@NonNull String message, @NonNull String code) {

        }

        /**
         * 需要注册
         *
         * @param message 错误信息
         * @param code    错误码
         */
        @Override
        public void needRegister(@NonNull String message, @NonNull String code) {
        }

        /**
         * 处理强提示错误
         *
         * @param message 错误信息
         * @param code    错误码
         */
        @Override
        public void needShowStrongError(@NonNull String message, @NonNull String code) {

        }

        /**
         * 其他错误处理
         *
         * @param exception 响应数据
         * @param code      错误码
         * @param message   错误信息
         */
        @Override
        public void otherError(@NonNull NetException exception, String code, @NonNull String message) {

        }
    }

    public interface ErrorCallback {

        /**
         * 请求成功
         *
         * @param response 响应数据
         */
        void success(@NonNull JSONObject response);

         /**
         * 需要登录
         *
         * @param message
         * @param code
         */
        void needLogin(@NonNull String message, @NonNull String code);

        /**
         * 当前错误需要重新登陆
         *
         * @param message 错误信息
         * @param code    错误码
         */
        void needRegister(@NonNull String message, @NonNull String code);

        /**
         * 处理强提示错误
         *
         * @param message 错误信息
         * @param code    错误码
         */
        void needShowStrongError(@NonNull String message, @NonNull String code);

        /**
         * 其他错误处理
         *
         * @param exception 响应数据
         * @param code      错误码
         * @param message   错误信息
         */
        void otherError(@NonNull NetException exception, String code, @NonNull String message);


    }
}
