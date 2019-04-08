package com.link.feeling.framework.utils.data;

import android.util.Log;

import com.link.feeling.framework.base.BaseApplication;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * Created on 2019/1/3  14:59
 * chenpan pan.chen@linkfeeling.cn
 *
 * 日志
 */
@SuppressWarnings("unused")
public final class L {
    public static String TAG = BaseApplication.getAppContext().getPackageName();
    // public static boolean DEBUG = BuildConfig.DEBUG;
    public static boolean DEBUG = true;

    static {
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    public static int v(String msg) {
        if (DEBUG) {
            return Log.v(TAG, msg);
        }
        return 0;
    }

    public static int w(String msg) {
        if (DEBUG) {
            return Log.w(TAG, msg);
        }
        return 0;
    }

    public static int e(String msg) {
        if (DEBUG) {
            //Logger.e(msg);
            return Log.e(TAG, msg);
        }
        return 0;
    }

    public static void result(String msg) {
        if (DEBUG) {
            Logger.json(msg);
            //return Log.e(TAG, msg);
        }
    }

    public static int d(String msg) {
        if (DEBUG) {
            return Log.d(TAG, msg);
        }
        return 0;
    }

    public static int i(String msg) {
        if (DEBUG) {
            return Log.i(TAG, msg == null ? "info msg is null" : msg);
        }
        return 0;
    }

    public static int v(String tag, String msg) {
        if (DEBUG) {
            return Log.v(tag, msg);
        }
        return 0;
    }

    public static int v(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.v(tag, msg, tr);
        }
        return 0;
    }

    public static int d(String tag, String msg) {
        if (DEBUG) {
            return Log.d(tag, msg);
        }
        return 0;
    }

    public static int d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.d(tag, msg, tr);
        }
        return 0;
    }


    //过长换行
    public static int e(String tag, String msg) {
        if (DEBUG) {
            //越界
            msg = msg.trim();
            int index = 0;
            int maxLength = 3000;
            String sub;
            while (index < msg.length()) {
                // java的字符不允许指定超过总的长度end
                if (msg.length() <= index + maxLength) {
                    sub = msg.substring(index);
                } else {
                    sub = msg.substring(index, index + maxLength);
                }
                index += maxLength;
                Log.e(tag, sub);
            }
        }
        return 0;
    }
//    public static int e(String tag, String msg) {
//        if (DEBUG) {
//              return   Log.e(tag, msg);
//        }
//        return 0;
//    }

    public static int e(String tag, Throwable msg) {
        if (DEBUG) {
            return Log.e(tag, msg.getMessage());
        }
        return 0;
    }

    public static int e(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.e(tag, msg, tr);
        }
        return 0;
    }

    public static int w(String tag, String msg) {
        if (DEBUG) {
            return Log.w(tag, msg);
        }
        return 0;
    }

    public static int w(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.w(tag, msg, tr);
        }
        return 0;
    }

    public static int i(String tag, String msg) {
        if (DEBUG) {
            return Log.i(tag, msg);
        }
        return 0;
    }

    public static int i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            return Log.i(tag, msg, tr);
        }
        return 0;
    }
}
