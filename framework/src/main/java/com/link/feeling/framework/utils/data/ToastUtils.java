package com.link.feeling.framework.utils.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.link.feeling.framework.base.BaseApplication;

/**
 * Created on 2019/1/14  16:47
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class ToastUtils {

    private static Toast sToast;
    private static Context sApplicationContext = BaseApplication.getAppContext();

    private ToastUtils(){
        throw new UnsupportedOperationException("工具类不能调用构造函数");
    }

    public static void showToast(@NonNull final String text) {
        if (StringUtils.isEmpty(text)) {
            return;
        }
        if (sToast == null) {
            ensureToastCreate(sApplicationContext);
        }
        sToast.setText(text);
        sToast.show();
    }

    public static void showToast(@StringRes final int text) {
        showToast(sApplicationContext.getString(text));
    }

    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

    @SuppressLint("ShowToast")
    private static void ensureToastCreate(@NonNull Context context) {
        if (sToast == null) {
            sToast = Toast.makeText(context.getApplicationContext(), "", Toast.LENGTH_SHORT);
        }
    }
}
