package cn.linkfeeling.hankserve.subjects;


import android.os.ParcelUuid;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.AccelData;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.MatchResult;
import cn.linkfeeling.hankserve.bean.NDKTools;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.Power;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.WatchData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.queue.MatchQueue;
import cn.linkfeeling.hankserve.queue.UwbQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 飞鸟数据解析
 */
public class FlyBirdProcessor implements IDataAnalysis {
    public static ConcurrentHashMap<String, FlyBirdProcessor> map;
    private static final float SELF_GRAVITY = 2.5f;
    private LimitQueue<Integer> limitQueue = new LimitQueue<>(50);
    private MatchQueue<Byte> devicesQueue = new MatchQueue<>(130);
    private LimitQueue<Integer> seqQueue = new LimitQueue<>(130);

    private int flag = -1;

    private volatile boolean start = true;
    private volatile boolean select = true;
    private long startTime;

    static {
        map = new ConcurrentHashMap<>();
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
        Log.i("999999999" + bleName + "--" + hostName, Arrays.toString(serviceData));

        if (serviceData == null) {
            return null;
        }


        //   dealPowerData(serviceData, deviceByBleName, bleName);
//        if(serviceData[0]!=0 && serviceData[0]!=-1){
//            deviceByBleName.setAbility(serviceData[0]);
//        }
        byte[] seqNum = {serviceData[11], serviceData[12]};

        if (CalculateUtil.byteArrayToInt(seqNum) < flag && flag - CalculateUtil.byteArrayToInt(seqNum) < 10000) {
            return null;
        }

        if (limitQueue.contains(CalculateUtil.byteArrayToInt(seqNum))) {
            return null;
        }
        Log.i("seqNum", CalculateUtil.byteArrayToInt(seqNum) + "");
        limitQueue.offer(CalculateUtil.byteArrayToInt(seqNum));

        boolean b = dealPowerData(serviceData, deviceByBleName, bleName);
        if (b) {
            return null;
        }


        if (select && devicesQueue.size() == 130) {
            select = false;
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> map = FinalDataManager.getInstance().getMatchTemp().get(deviceByBleName.getFencePoint().getFenceId());

            byte[] deviceData = new byte[130];
            List<Byte> deviceList = new ArrayList<>(devicesQueue);
            List<Integer> seqList = new ArrayList<>(seqQueue);
            Log.i("pipeizhimmmm", new Gson().toJson(deviceList));
            Log.i("pipeizhissss", new Gson().toJson(seqList));
            Log.i("pipeizhizzzz", map.size() + "");


            for (int i = 0; i < deviceList.size(); i++) {
                deviceData[i] = deviceList.get(i);
            }

            for (UWBCoordData next : map.keySet()) {
                WristbandProcessor wristbandProcessor = WristbandProcessor.map.get(LinkDataManager.getInstance().getUwbCode_wristbandName().get(next.getCode()));
                if (wristbandProcessor != null) {
                    MatchQueue<AccelData> wristQueue = wristbandProcessor.getWatchQueue();
                    WatchData watchData = new WatchData();
                    AccelData[] accelData = new AccelData[40];

                    LimitQueue<Integer> watchSeq = wristbandProcessor.getWatchSeq();

                    List<AccelData> watchList = new ArrayList<>(wristQueue);
                    List<Integer> watchSeqList = new ArrayList<>(watchSeq);

                    Log.i("pipeizhinnnnn", new Gson().toJson(watchList));
                    Log.i("pipeizhiwwwww", new Gson().toJson(watchSeqList));
                    for (int i = 0; i < watchList.size(); i++) {
                        accelData[i] = watchList.get(i);
                    }
                    watchData.setData(accelData);
                    int matchNum = NDKTools.match_data(deviceData, watchData);
                    Log.i("pipeizhi-----", matchNum + "");

                    MatchResult matchResult = new MatchResult();
                    matchResult.setDeviceName(deviceByBleName.getDeviceName());
                    String name = LinkDataManager.getInstance().getUwbCode_wristbandName().get(next.getCode());
                    matchResult.setWristband(name == null ? "" : name);
                    matchResult.setMatchResult(matchNum);
                    EventBus.getDefault().post(matchResult);
                }
            }
        }


        if (start) {
            FinalDataManager.getInstance().removeRssi(deviceByBleName.getAnchName());
            startTime = System.currentTimeMillis();
            ConcurrentHashMap<String, UwbQueue<Point>> spareTire = LinkDataManager.getInstance().queryQueueByDeviceId(deviceByBleName.getId());
            if (spareTire.isEmpty()) {
                start = false;
                return null;
            }
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> queueConcurrentHashMap = new ConcurrentHashMap<>();
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> tempHashMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, UwbQueue<Point>> next : spareTire.entrySet()) {
                String key = next.getKey();
                UWBCoordData uwbCoordData = new UWBCoordData();
                uwbCoordData.setCode(key);
                uwbCoordData.setSemaphore(0);
                uwbCoordData.setDevice(deviceByBleName);
                queueConcurrentHashMap.put(uwbCoordData, next.getValue());
                tempHashMap.put(uwbCoordData, next.getValue());
            }
            FinalDataManager.getInstance().getAlternative().put(deviceByBleName.getFencePoint().getFenceId(), queueConcurrentHashMap);
            FinalDataManager.getInstance().getMatchTemp().put(deviceByBleName.getFencePoint().getFenceId(), tempHashMap);
            start = false;
        }


        if (!FinalDataManager.getInstance().alreadyBind(deviceByBleName.getFencePoint().getFenceId())) {
            //System.currentTimeMillis() - startTime >= 5 * 1000
            if (serviceData[13] > 0) {
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

        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);


        if (serviceData[0] != -1 && serviceData[0] != 0 && serviceData[1] != -1 && serviceData[1] != 0) {

            for (int j = 0; j < 10; j++) {
                int cuv1 = CalculateUtil.byteToInt(serviceData[j]);
                if (bleDeviceInfoNow != null) {
                    bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
                    bleDeviceInfoNow.getCurve().add(cuv1);
                    bleDeviceInfoNow.setSeq_num(String.valueOf(CalculateUtil.byteArrayToInt(seqNum)));
                }

                if (serviceData[13] != 0) {
                    devicesQueue.offer(serviceData[j]);
                }

            }

            if (serviceData[13] != 0) {
                seqQueue.offer(CalculateUtil.byteArrayToInt(seqNum));
            }

        }


        if (serviceData[0] == -1 && serviceData[1] == -1) {
            start = true;
            select = true;
            devicesQueue.clear();
            seqQueue.clear();
            FinalDataManager.getInstance().getMatchTemp().clear();
            flag = CalculateUtil.byteArrayToInt(seqNum);

            if (serviceData[10] == 0 || serviceData[13] == 0) {
                //       deviceByBleName.setAbility(0);
                return null;
            }
            byte act_time = serviceData[13];
            byte gravity = serviceData[10];
            float actualGravity = SELF_GRAVITY * gravity;
            byte u_time = serviceData[14];

            if (bleDeviceInfoNow != null) {
                bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
                bleDeviceInfoNow.setGravity(String.valueOf(actualGravity));
                bleDeviceInfoNow.setTime(String.valueOf(act_time));
                bleDeviceInfoNow.setU_time(String.valueOf(CalculateUtil.byteToInt(u_time)));
                bleDeviceInfoNow.setSeq_num(String.valueOf(CalculateUtil.byteArrayToInt(seqNum)));
                //    deviceByBleName.setAbility(0);
            }

        }
        return bleDeviceInfoNow;

    }

    private boolean dealPowerData(byte[] serviceData, LinkSpecificDevice deviceByBleName, String bleName) {
        //  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
        if (serviceData[0] == 0 &&
                serviceData[1] == 0 &&
                serviceData[2] == 0 &&
                serviceData[3] == 0 &&
                serviceData[4] == 0 &&
                serviceData[5] == 0 &&
                serviceData[6] == 0 &&
                serviceData[7] == 0 &&
                serviceData[8] == 0 &&
                serviceData[9] == 0 &&
                serviceData[10] == 0 &&
                serviceData[13] == 0 &&
                serviceData[14] == 0) {

            Power power1 = new Power();
            power1.setDeviceName(deviceByBleName.getDeviceName());
            power1.setBleNme(bleName);
            power1.setPowerLevel(CalculateUtil.byteToInt(serviceData[15]));
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
