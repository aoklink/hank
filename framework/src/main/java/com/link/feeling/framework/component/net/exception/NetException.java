package com.link.feeling.framework.component.net.exception;

import android.text.TextUtils;

import com.link.feeling.framework.component.net.NetResult;

/**
 * Created on 2019/1/17  11:42
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class NetException extends Exception {
    private String code;

    private String errorMsg;

    public NetException(Exception exception) {
        super(exception);
    }

    public NetException(NetResult<?> netResult) {
        code = netResult.code;
        errorMsg = netResult.msg;
    }

    public String code() {
        return code;
    }

    public String errorMsg() {
        return errorMsg;
    }

    @Override
    public String getMessage() {
        String defaultMsg = errorMsg();
        if (TextUtils.isEmpty(defaultMsg)) {
            return super.getMessage();
        } else {
            return defaultMsg;
        }
    }

}