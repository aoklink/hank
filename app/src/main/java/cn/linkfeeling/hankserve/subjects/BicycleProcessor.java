package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 单车数据解析
 */
public class BicycleProcessor implements IDataAnalysis {
    public static ConcurrentHashMap<String, BicycleProcessor> map;
    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);

    static {
        map = new ConcurrentHashMap<>();
    }


    public static BicycleProcessor getInstance() {
        return BicycleProcessorHolder.sBicycleProcessor;
    }

    private static class BicycleProcessorHolder {
        private static final BicycleProcessor sBicycleProcessor = new BicycleProcessor();
    }


    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow;
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (scanRecord == null || linkScanRecord == null || deviceByBleName == null) {
            return null;
        }
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        if (serviceData == null) {
            return null;
        }

        Log.i("danchedata", Arrays.toString(serviceData));


        byte seq = serviceData[4];
        if (limitQueue.contains(CalculateUtil.byteToInt(seq))) {
            return null;
        }
        limitQueue.offer(CalculateUtil.byteToInt(seq));

        byte[] turns = new byte[2];
        turns[0] = serviceData[0];
        turns[1] = serviceData[1];


        byte[] ticks = new byte[2];
        ticks[0] = serviceData[3];
        ticks[1] = serviceData[2];

        float speed;
        if (turns[0] == -1 && turns[1] == -1) {
            speed = 0;
        } else if (CalculateUtil.byteArrayToInt(ticks) == 0) {
            return null;
        } else {
            BigDecimal bigDecimal = CalculateUtil.floatDivision(deviceByBleName.getPerimeter(), (float) CalculateUtil.byteArrayToInt(ticks));
            //  speed = calculateBicycleSpeed(bigDecimal.floatValue() * 3600, deviceByBleName.getSlope());
            speed = bigDecimal.floatValue() * 3600;
        }
        Log.i("ticks", speed + "");
        Log.i("ticks----", (float) CalculateUtil.byteArrayToInt(ticks) + "");
        Log.i("ticks===", Arrays.toString(ticks));

        deviceByBleName.setAbility(speed);

        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow == null) {
            return null;
        }


        bleDeviceInfoNow.setSpeed(String.valueOf(speed));
        bleDeviceInfoNow.setSeq_num(String.valueOf(CalculateUtil.byteToInt(seq)));
        return bleDeviceInfoNow;

    }

    /**
     * 根据转速换算速度(单车)
     *
     * @author zhangyong
     * @time 2019/3/18 14:01
     */
    private double calculateBicycleSpeed(int rotate) {
        float v = (float) ((rotate * 0.3378) - 7.3649);
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }

    private float calculateBicycleSpeed(float measureSpeed, float slope) {
        BigDecimal bigDecimal = CalculateUtil.floatDivision(measureSpeed, slope);

        float v = (float) ((bigDecimal.floatValue() * 0.3378) - 7.3649);
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }

}
