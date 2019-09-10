package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.Power;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.queue.UwbQueue;
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
    private int flag = -1;

    private volatile boolean start = true;
    private volatile boolean select = true;
    private long startTime;

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
    public BleDeviceInfo analysisBLEData(String hostName, byte[] scanRecord, String bleName) {
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

        byte[] seqNum = {serviceData[4], serviceData[5]};

        if (CalculateUtil.byteArrayToInt(seqNum) < flag && flag - CalculateUtil.byteArrayToInt(seqNum) < 10000) {
            return null;
        }

        if (limitQueue.contains(CalculateUtil.byteArrayToInt(seqNum))) {
            return null;
        }
        Log.i("dancheseqNum", CalculateUtil.byteArrayToInt(seqNum) + "");
        limitQueue.offer(CalculateUtil.byteArrayToInt(seqNum));

        if (start) {
            FinalDataManager.getInstance().removeRssi(deviceByBleName.getAnchName());
            startTime = System.currentTimeMillis();
            start = false;
        }


        if (select && System.currentTimeMillis() - startTime >= 5 * 1000) {
            ConcurrentHashMap<String, UwbQueue<Point>> spareTire = LinkDataManager.getInstance().queryQueueByDeviceId(deviceByBleName.getId());
            if (spareTire == null || spareTire.isEmpty()) {
                Log.i("pppppppp", "-5-5-5");
                select = false;
                return null;
            }

            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> queueConcurrentHashMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, UwbQueue<Point>> next : spareTire.entrySet()) {
                String key = next.getKey();
                UWBCoordData uwbCoordData = new UWBCoordData();
                uwbCoordData.setCode(key);
                uwbCoordData.setSemaphore(0);
                uwbCoordData.setDevice(deviceByBleName);
                queueConcurrentHashMap.put(uwbCoordData, next.getValue());

            }
            Log.i("pppppppp6666", queueConcurrentHashMap.size() + "");

            FinalDataManager.getInstance().getAlternative().put(deviceByBleName.getFencePoint().getFenceId(), queueConcurrentHashMap);
            select = false;
        }

        if (!FinalDataManager.getInstance().alreadyBind(deviceByBleName.getFencePoint().getFenceId())) {
            if (System.currentTimeMillis() - startTime >= 5 * 1000) {
                String s = FinalDataManager.getInstance().getRssi_wristbands().get(deviceByBleName.getAnchName());
                if (s != null) {
                    String uwbCode = LinkDataManager.getInstance().queryUWBCodeByWristband(s);
                    if (uwbCode != null && !FinalDataManager.getInstance().alreadyBind(uwbCode)) {
                        LinkDataManager.getInstance().bleBindAndRemoveSpareTire(uwbCode, deviceByBleName);
                    }
                } else {
                    LinkDataManager.getInstance().checkBind(deviceByBleName);

                }
            }
        }


        byte[] turns = new byte[2];
        turns[0] = serviceData[0];
        turns[1] = serviceData[1];


        byte[] ticks = new byte[2];
        ticks[0] = serviceData[2];
        ticks[1] = serviceData[3];

        float speed;
        if (CalculateUtil.byteArrayToInt(ticks) == 0) {
            flag = CalculateUtil.byteArrayToInt(seqNum);
            speed = 0;

        } else {
            BigDecimal bigDecimal = CalculateUtil.floatDivision(deviceByBleName.getPerimeter(), (float) CalculateUtil.byteArrayToInt(ticks));
            speed = calculateBicycleSpeed(bigDecimal.floatValue() * 3600, deviceByBleName.getSlope());
        }
        Log.i("ticks", speed + "");
        Log.i("ticks----", (float) CalculateUtil.byteArrayToInt(ticks) + "");
        Log.i("ticks===", Arrays.toString(ticks));

        Log.i("00000000000---", speed + "");
        //    deviceByBleName.setAbility(speed);

        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow != null) {
            Log.i("00000000000", "null");
            bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
            bleDeviceInfoNow.setSpeed(String.valueOf(speed));
            bleDeviceInfoNow.setSeq_num(String.valueOf(CalculateUtil.byteArrayToInt(seqNum)));
        }


        Log.i("00000000000dddd", speed + "");

        if (speed == 0) {
            start = true;
            select = true;
            //解除绑定
            int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
            if (FinalDataManager.getInstance().getFenceId_uwbData().containsKey(fenceId)) {
                FinalDataManager.getInstance().removeUwb(fenceId);
            }
            FinalDataManager.getInstance().getAlternative().remove(fenceId);
        }
        return bleDeviceInfoNow;

    }


    /**b
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
        float v;
        BigDecimal bigDecimal = CalculateUtil.floatDivision(measureSpeed, slope);
        if (bigDecimal.floatValue() > 90) {
            v = (float) ((bigDecimal.floatValue() * bigDecimal.floatValue()) * 0.0033 - 0.194 * bigDecimal.floatValue() + 13.33);
            //  v = (float) ((bigDecimal.floatValue() * 0.666) - 42.15);//向南提供的函数关系
        } else {
            v = (float) ((bigDecimal.floatValue() * 0.329) - 7.01);
        }
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }

    //[0, 0, 0, 0, 1, 117, 3, 5]

    private boolean dealPowerData(byte[] serviceData, LinkSpecificDevice deviceByBleName, String bleName) {
        //  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
        if (serviceData[0] == 0 &&
                serviceData[1] == 0 &&
                serviceData[2] == 0 &&
                serviceData[3] == 0) {

            Power power1 = new Power();
            power1.setDeviceName(deviceByBleName.getDeviceName());
            power1.setBleNme(bleName);
            power1.setPowerLevel(CalculateUtil.byteToInt(serviceData[6]));
            power1.setGymName(BuildConfig.PROJECT_NAME);


            power1.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {

                    Log.i("99999-----", s == null ? "null" : s);
                    Log.i("99999eeeee", e == null ? "null" : e.getMessage());
                }
            });
            return true;
        }
        return false;
    }

}
