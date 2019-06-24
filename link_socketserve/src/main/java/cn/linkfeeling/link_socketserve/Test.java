package cn.linkfeeling.link_socketserve;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;

import cn.linkfeeling.link_socketserve.bean.ScanData;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;

/**
 * @author create by zhangyong
 * @time 2019/5/25
 */
public class Test {
    public static void main(String args[]) {

        Gson gson = new Gson();
        ScanData scanData = new ScanData();
        scanData.setName("123321");

        String s = gson.toJson(scanData);
        ScanData scanData1 = gson.fromJson(s, ScanData.class);

        Log.i("cccccccccc", scanData.toString() + "===" + scanData1.toString());


    }

    public static short getShort(byte[] bytes) {
        return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
    }

    public static byte[] putShort(short s) {
        byte[] b = new byte[2];
        b[1] = (byte) (s >> 8);
        b[0] = (byte) (s >> 0);

        return b;
    }
}
