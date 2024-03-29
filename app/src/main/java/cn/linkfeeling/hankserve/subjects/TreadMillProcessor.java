package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;

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
import cn.linkfeeling.hankserve.bean.TreadmillError;
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
 * 跑步机数据解析
 */
public class TreadMillProcessor implements IDataAnalysis {

    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);
    public static ConcurrentHashMap<String, TreadMillProcessor> map;
    private int flag = -1;

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
    public synchronized BleDeviceInfo analysisBLEData(String hostName, byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow;
        BleDeviceInfo bleDeviceInfo = null;
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (scanRecord == null || linkScanRecord == null || deviceByBleName == null) {
            return null;
        }
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        if (serviceData == null) {
            return null;
        }
        byte[] pages = new byte[2];
        pages[0] = serviceData[2];
        pages[1] = serviceData[3];
        int nowPack = CalculateUtil.byteArrayToInt(pages);

        Log.i("87878787" + bleName, Arrays.toString(serviceData));
        Log.i("87878787" + bleName, "flag---" + flag);
        Log.i("87878787" + bleName, "seq---" + nowPack);

        if (nowPack < flag && flag - nowPack < 10000) {
            return null;
        }

        if (limitQueue.contains(nowPack)) {
            return null;
        }
        limitQueue.offer(nowPack);

        Log.i("87878787" + bleName, "seq---" + nowPack);

        if (start) {
            FinalDataManager.getInstance().removeRssi(deviceByBleName.getAnchName());
            startTime = System.currentTimeMillis();
            int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
            FinalDataManager.getInstance().getAlternative().remove(fenceId);
            start = false;
        }

        if (select && System.currentTimeMillis() - startTime >= 10 * 1000) {
            ConcurrentHashMap<String, UwbQueue<Point>> spareTire = LinkDataManager.getInstance().queryQueueByDeviceId(deviceByBleName.getId());
            if (spareTire != null && !spareTire.isEmpty()) {
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
            }
            select = false;
        }


        if (!FinalDataManager.getInstance().alreadyBind(deviceByBleName.getFencePoint().getFenceId())) {
            if (System.currentTimeMillis() - startTime >= 10 * 1000) {
                String s = FinalDataManager.getInstance().getRssi_wristbands().get(deviceByBleName.getAnchName());
                if (s != null
                        && !FinalDataManager.getInstance().getDevice_wristbands().values().contains(s)
                        && FinalDataManager.getInstance().getWebAccounts().contains(s)) {
                    String uwbCode = LinkDataManager.getInstance().queryUWBCodeByWristband(s);
                    if (uwbCode != null && !FinalDataManager.getInstance().alreadyBind(uwbCode)) {
                        LinkDataManager.getInstance().bleBindAndRemoveSpareTire(uwbCode, deviceByBleName);
                    }
                } else {
                    Log.i("binding--", "kaishi");
                    LinkDataManager.getInstance().checkBind(deviceByBleName);
                }

            }

        }


        Log.i("6767676", Arrays.toString(serviceData));
        //检查是否有可绑定的手环  如果有则根据算法匹配


        float speed;
        if (serviceData[0] == -1 && serviceData[1] == -1) {
            flag = nowPack;
            speed = 0;
        } else {
            byte[] serviceDatum = new byte[2];
            serviceDatum[0] = serviceData[11];
            serviceDatum[1] = serviceData[12];
            int numbers = CalculateUtil.byteArrayToInt(serviceDatum);
            Log.i("67676", numbers + "");
            float v = CalculateUtil.txFloat(numbers, 100);

            speed = v * 0.25641026f;
            //0.256410

            if (speed > 20) {
                TreadmillError treadmillError = new TreadmillError();
                treadmillError.setGymName(BuildConfig.GYM_NAME);
                treadmillError.setDeviceName(deviceByBleName.getDeviceName());
                treadmillError.setBleName(bleName);
                treadmillError.setNumbers(numbers);
                treadmillError.setRawData(serviceData);
                treadmillError.setSpeedData(serviceDatum);
                treadmillError.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {

                    }
                });
            }
        }

        Log.i("6767676", speed + "");


        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow != null) {
            Log.i("3333333", "我是原始绑定---" + speed);
            bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
            bleDeviceInfoNow.setSpeed(String.valueOf(speed));
            bleDeviceInfoNow.setSeq_num(String.valueOf(nowPack));
        }
        bleDeviceInfo = LinkDataManager.getInstance().getWebFinalBind(deviceByBleName);

        if (bleDeviceInfo != null) {
            Log.i("3333333", "我是数据来源---" + speed);
            bleDeviceInfo.setDevice_name(deviceByBleName.getDeviceName());
            bleDeviceInfo.setSpeed(String.valueOf(speed));
            bleDeviceInfo.setSeq_num(String.valueOf(nowPack));
        }

        if (speed == 0) {
            start = true;
            select = true;
            if (bleDeviceInfo != null) {
                LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
            }
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
