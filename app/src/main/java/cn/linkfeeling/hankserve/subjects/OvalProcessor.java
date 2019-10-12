package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;

import com.link.feeling.framework.utils.data.ToastUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Point;
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
 * 椭圆机数据解析
 */
public class OvalProcessor implements IDataAnalysis {

    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);
    public static ConcurrentHashMap<String, OvalProcessor> map;
    private int flag = -1;
    private volatile boolean start = true;
    private volatile boolean select = true;
    private long startTime;

    static {
        map = new ConcurrentHashMap<>();
    }


    public static OvalProcessor getInstance() {
        return OvalProcessorHolder.sOvalProcessor;
    }


    private static class OvalProcessorHolder {
        private static final OvalProcessor sOvalProcessor = new OvalProcessor();
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

        Log.i("vvvvvvv", Arrays.toString(serviceData));

        byte[] seqNum = {serviceData[4], serviceData[5]};
        if (CalculateUtil.byteArrayToInt(seqNum) < flag && flag - CalculateUtil.byteArrayToInt(seqNum) < 10000) {
            return null;
        }


        if (limitQueue.contains(CalculateUtil.byteArrayToInt(seqNum))) {
            return null;
        }
        Log.i("tuoyuanjiseqNum", CalculateUtil.byteArrayToInt(seqNum) + "");
        limitQueue.offer(CalculateUtil.byteArrayToInt(seqNum));
        boolean b = dealPowerData(serviceData, deviceByBleName, bleName);
        if (b) {
            return null;
        }


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
            Log.i("lllllll" + bleName, CalculateUtil.byteArrayToInt(ticks) + "");
            speed = bigDecimal.floatValue() * 3600;

        }
        Log.i("ticks", speed + "");


        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow != null) {
            bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
            bleDeviceInfoNow.setSpeed(String.valueOf(speed));
            bleDeviceInfoNow.setSeq_num(String.valueOf(CalculateUtil.byteArrayToInt(seqNum)));
        }

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

    /**
     * 计算椭圆机的速度（椭圆机）
     *
     * @author zhangyong
     * @time 2019/3/18 16:44
     */
    private float calculateEllipticalSpeed(int rotate) {
        float v = (float) (1.837 * Math.pow(Math.E, (0.0259 * rotate)));
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }


    private float calculateEllipticalSpeed(float measureSpeed, float slope) {
        BigDecimal bigDecimal = CalculateUtil.floatDivision(measureSpeed, slope);
        float v = (float) (1.837 * Math.pow(Math.E, (0.0259 * bigDecimal.floatValue())));
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }

    //[0, 0, 0, 0, 1, 117, 3, 5]

    private boolean dealPowerData(byte[] serviceData, LinkSpecificDevice deviceByBleName, String bleName) {
        if (serviceData[0] == 0 &&
                serviceData[1] == 0 &&
                serviceData[2] == 0 &&
                serviceData[3] == 0) {

            DevicePower.DataBean dataBean = new DevicePower.DataBean();
            dataBean.setSerial_no(String.valueOf(1));
            dataBean.setDevice_id(bleName);
            dataBean.setDevice(deviceByBleName.getDeviceName());
            int powerLevel = CalculateUtil.byteToInt(serviceData[6]);
            dataBean.setBattery(String.valueOf(100 / powerLevel));
            FinalDataManager.getInstance().getBleName_dateBean().put(bleName, dataBean);

/*

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
            });*/
            return true;
        }
        return false;
    }
}
