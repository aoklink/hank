package cn.linkfeeling.hankserve.subjects;

import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.utils.HexUtil;
import cn.linkfeeling.hankserve.utils.ScanRecordUtil;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 手环数据解析
 */
public class WristbandProcessor implements IDataAnalysis {

    public static WristbandProcessor getInstance() {
        return WristbandProcessor.WristbandProcessorHolder.sWristbandProcessor;
    }

    private static class WristbandProcessorHolder {
        private static final WristbandProcessor sWristbandProcessor = new WristbandProcessor();
    }


    @Override
    public BleDeviceInfo analysisBLEData(BleDeviceInfo bleDeviceInfo, byte[] bytes, String bleName) {



        if (bytes == null) {
            return null;
        }
        ScanRecordUtil scanRecordUtil = ScanRecordUtil.parseFromBytes(bytes);
        if (scanRecordUtil == null) {
            return null;
        }

        Log.i("hhhhhhhhhhhhhhhh"+bleName,Arrays.toString(bytes));

        SparseArray<byte[]> manufacturerSpecificData = scanRecordUtil.getManufacturerSpecificData();
        if (manufacturerSpecificData != null && manufacturerSpecificData.size() != 0) {
            byte[] bytes1 = manufacturerSpecificData.valueAt(0);
            if (bytes1 == null || bytes1.length == 0) {
                return null;
            }
            byte[] heart = new byte[1];
            heart[0] = bytes1[0];
           int heartInt = Integer.parseInt(HexUtil.encodeHexStr(heart), 16);
            String heatRate = String.valueOf(heartInt);

            Log.i("cccccccccccccccc"+bleName,heatRate);
            bleDeviceInfo.setBracelet_id(bleName);
            bleDeviceInfo.setHeart_rate(heatRate);
        }

        return bleDeviceInfo;
    }
}