package cn.linkfeeling.hankserve.bean;

public class NDKTools {

    static {
        System.loadLibrary("MY_JNI");
    }

    public static native String getStringFromNDK();

    public static native int match_data(byte[] device_data,short de_data_len, WatchData watchdata,short wa_data_len);


}