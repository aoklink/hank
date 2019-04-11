package cn.linkfeeling.hankserve.subjects;

import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.ScanRecordUtil;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 飞鸟数据解析
 */
public class FlyBirdProcessor implements IDataAnalysis {
    private int serialNum = -1;


    public static FlyBirdProcessor getInstance() {
        return FlyBirdProcessorHolder.sFlyBirdProcessor;
    }

    private static class FlyBirdProcessorHolder {
        private static final FlyBirdProcessor sFlyBirdProcessor = new FlyBirdProcessor();
    }

    @Override
    public BleDeviceInfo analysisBLEData(final BleDeviceInfo bleDeviceInfo, byte[] scanRecord, String bleName) {

        if (scanRecord == null) {
            return null;
        }


        ScanRecordUtil scanRecordUtil = ScanRecordUtil.parseFromBytes(scanRecord);
        if (scanRecordUtil == null) {
            return null;
        }


        SparseArray<byte[]> manufacturerSpecificData = scanRecordUtil.getManufacturerSpecificData();
        if (manufacturerSpecificData != null && manufacturerSpecificData.size() != 0) {
            final byte[] bytes1 = manufacturerSpecificData.valueAt(0);
            Log.i("fly bird", bleName + "-----" + Arrays.toString(bytes1));

            LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
            if (deviceByBleName == null) {
                return null;
            }
            deviceByBleName.setAbility(bytes1[0]);

            if (bytes1[0] == 0 && bytes1[12] == 0 && bytes1[14] == 0) {
                return null;
            }


            if (serialNum == bytes1[10]) {
                return null;
            }
            serialNum = bytes1[10];


            //在拉伸的过程中  原始数据不为0  但是质量和次数为0
            if (bytes1[12] == 0 || bytes1[14] == 0) {
                return null;
            }


            byte[] gravityBtye=new byte[1];
            gravityBtye[0]=bytes1[12];
            int gravity = byteArrayToInt(gravityBtye);


            float v = CalculateUtil.txFloat(gravity, 10);
            bleDeviceInfo.setGravity(String.valueOf(v));
            Log.i("lllllllll",String.valueOf(v));
            bleDeviceInfo.setTime(String.valueOf(bytes1[14]));
        }
        return bleDeviceInfo;

    }


    /**
     * 将4字节的byte数组转成一个int值
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b){
        byte[] a = new byte[4];
        int i = a.length - 1,j = b.length - 1;
        for (; i >= 0 ; i--,j--) {//从b的尾部(即int值的低位)开始copy数据
            if(j >= 0)
                a[i] = b[j];
            else
                a[i] = 0;//如果b.length不足4,则将高位补0
        }
        int v0 = (a[0] & 0xff) << 24;//&0xff将byte值无差异转成int,避免Java自动类型提升后,会保留高位的符号位
        int v1 = (a[1] & 0xff) << 16;
        int v2 = (a[2] & 0xff) << 8;
        int v3 = (a[3] & 0xff) ;
        return v0 + v1 + v2 + v3;
    }
}
