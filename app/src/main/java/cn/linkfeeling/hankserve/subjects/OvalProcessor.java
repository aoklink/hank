package cn.linkfeeling.hankserve.subjects;

import android.util.Log;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.UwbQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 椭圆机数据解析
 */
public class OvalProcessor implements IDataAnalysis {

    public static ConcurrentHashMap<String, OvalProcessor> map;
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
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow;
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (scanRecord == null || deviceByBleName == null) {
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

        Log.i("tttttttttttttt", Arrays.toString(scanRecord));
        byte[] speed = new byte[1];
        byte[] gradient = new byte[2];
        speed[0] = scanRecord[11];
        //  speed[1] = scanRecord[12];
        gradient[0] = scanRecord[13];
        gradient[1] = scanRecord[14];

        int speedInt = Integer.parseInt(String.valueOf(CalculateUtil.byteArrayToInt(speed)));
        int gradientInt = Integer.parseInt(String.valueOf(gradient[0]));


        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow != null) {
            bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
            if (speedInt == 0) {
                bleDeviceInfoNow.setSpeed("0.0");
            } else {
                bleDeviceInfoNow.setSpeed(String.valueOf(calculateEllipticalSpeed(speedInt)));
            }

            bleDeviceInfoNow.setGradient(String.valueOf(gradientInt));
        }

        if(speedInt==0){
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

}
