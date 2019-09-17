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

import static cn.linkfeeling.hankserve.utils.CalculateUtil.txFloat;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 跑步机数据解析
 */
public class TreadMillProcessor implements IDataAnalysis {


    public static ConcurrentHashMap<String, TreadMillProcessor> map;
    private volatile boolean start = true;
    private volatile boolean select = true;
    private long startTime;

    static {
        map = new ConcurrentHashMap<>();
    }

    public static TreadMillProcessor getInstance() {
        return TreadMillProcessorHolder.sTreadMillProcessor;
    }

    private static class TreadMillProcessorHolder {
        private static final TreadMillProcessor sTreadMillProcessor = new TreadMillProcessor();
    }

    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        Log.i("pppppppppppppp", Arrays.toString(scanRecord));
        BleDeviceInfo bleDeviceInfoNow;
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (scanRecord == null || deviceByBleName == null) {
            return null;
        }

        byte[] speed = new byte[1];
        speed[0] = scanRecord[11];
        int speedInt = CalculateUtil.byteArrayToInt(speed);


        if (start && speedInt > 0) {
            FinalDataManager.getInstance().removeRssi(deviceByBleName.getAnchName());
            startTime = System.currentTimeMillis();
            start = false;
        }
        if (select && speedInt > 0 && System.currentTimeMillis() - startTime >= 5 * 1000) {
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
            if (System.currentTimeMillis() - startTime >= 5 * 1000 && speedInt > 0) {
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


        byte[] gradient = new byte[2];
        gradient[0] = scanRecord[13];
        gradient[1] = scanRecord[14];


        int gradientInt = Integer.parseInt(String.valueOf(gradient[0]));


        Log.i("ooooooooooo", speedInt + "");

        float floatSpeed = txFloat(speedInt, 10);

        Log.i("ooooooooooo11", floatSpeed + "");
        float floatGradient = txFloat(gradientInt, 10);


        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow != null) {
            bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
            bleDeviceInfoNow.setSpeed(String.valueOf(floatSpeed));
            bleDeviceInfoNow.setGradient(String.valueOf(floatGradient));
        }
        if (speedInt == 0) {
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


}
