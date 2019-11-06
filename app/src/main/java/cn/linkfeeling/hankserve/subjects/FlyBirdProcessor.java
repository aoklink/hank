package cn.linkfeeling.hankserve.subjects;


import android.os.ParcelUuid;
import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.AccelData;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.FlagStatus;
import cn.linkfeeling.hankserve.bean.LinkBLE;
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
    private LimitQueue<Integer> limitQueue = new LimitQueue<>(50);

    private LimitQueue<Integer> deviceSeq = new LimitQueue<>(200);
    private List<Byte> devicesList = new ArrayList<>();

    private int flag = -1;
    private  boolean select = true;
    private  boolean start = true;
    private long startTime;
    private Gson gson = new Gson();

    static {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public synchronized BleDeviceInfo analysisBLEData(String hostName, byte[] scanRecord, String bleName) {
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

        byte[] seqNum = {serviceData[11], serviceData[12]};

        Log.i("87878787" + bleName, Arrays.toString(serviceData));
        Log.i("87878787" + bleName, "flag----" + flag);
        Log.i("87878787" + bleName, "seqNum----" + CalculateUtil.byteArrayToInt(seqNum));

        if (CalculateUtil.byteArrayToInt(seqNum) < flag && flag - CalculateUtil.byteArrayToInt(seqNum) < 10000) {
          //  uploadFlagStatus(CalculateUtil.byteArrayToInt(seqNum), flag, deviceByBleName, bleName);
            return null;
        }

        if (limitQueue.contains(CalculateUtil.byteArrayToInt(seqNum))) {
            return null;
        }
        Log.i("87878787" + bleName, CalculateUtil.byteArrayToInt(seqNum) + "");
        limitQueue.offer(CalculateUtil.byteArrayToInt(seqNum));

        boolean b = dealPowerData(serviceData, deviceByBleName, bleName);
        if (b) {
            return null;
        }

        if (start) {
            FinalDataManager.getInstance().removeRssi(deviceByBleName.getAnchName());
            startTime = System.currentTimeMillis();
            int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
            FinalDataManager.getInstance().getAlternative().remove(fenceId);
            devicesList.clear();

/*            ConcurrentHashMap<String, UwbQueue<Point>> spareTire = LinkDataManager.getInstance().queryQueueByDeviceId(deviceByBleName.getId());
            if (spareTire.isEmpty()) {
                start = false;
                return null;
            }
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> tempHashMap = new ConcurrentHashMap<>();
            for (Map.Entry<String, UwbQueue<Point>> next : spareTire.entrySet()) {
                String key = next.getKey();
                UWBCoordData uwbCoordData = new UWBCoordData();
                uwbCoordData.setCode(key);
                uwbCoordData.setSemaphore(0);
                uwbCoordData.setDevice(deviceByBleName);
                tempHashMap.put(uwbCoordData, next.getValue());
            }
            FinalDataManager.getInstance().getMatchTemp().put(deviceByBleName.getFencePoint().getFenceId(), tempHashMap);*/
            start = false;
        }

     /*   long diffTime = System.currentTimeMillis() - startTime;
        if (diffTime > 0 && (diffTime / 1000) >= 5 && (diffTime / 1000) % 5 == 0 && diffTime <= 60 * 1000) {
            //     Log.i("fly_match_time", diffTime + "");
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> map = FinalDataManager.getInstance().getMatchTemp().get(deviceByBleName.getFencePoint().getFenceId());
            byte[] deviceData = new byte[devicesList.size()];
            for (int i = 0; i < devicesList.size(); i++) {
                deviceData[i] = devicesList.get(i);
            }

            int second = (int) (diffTime / 1000);
            Log.i("fly_match_time_second", second + "");
            int watchDataNum = 6 * second;
            if (map != null && !map.isEmpty()) {
                Log.i("fly_match_sizeOfDevice", String.valueOf(devicesList.size()));
                Log.i("fly_match_device", gson.toJson(devicesList));
                List<Integer> dSeq = new ArrayList<>(deviceSeq);
                Log.i("fly_match_device_seq", gson.toJson(dSeq));
                boolean error = false;
                for (int i = 0; i < dSeq.size(); i++) {
                    if (i != dSeq.size() - 1) {
                        if ((dSeq.get(i + 1) - dSeq.get(i)) != 1 && (dSeq.get(i + 1) - dSeq.get(i)) != -65535) {

                            error = true;
                        }
                    }
                }
                if (error) {
                    Log.i("fly_match_stateOfDevice", "设备数据异常");
                } else {
                    Log.i("fly_match_stateOfDevice", "设备数据正常");

                }


                for (UWBCoordData next : map.keySet()) {
                    WristbandProcessor wristbandProcessor = WristbandProcessor.map.get(LinkDataManager.getInstance().getUwbCode_wristbandName().get(next.getCode()));
                    if (wristbandProcessor != null) {
                        WatchData watchData = new WatchData();
                        AccelData[] accelData = new AccelData[watchDataNum];
                        MatchQueue<AccelData> wristQueue = wristbandProcessor.getWatchQueue();
                        List<AccelData> watchList = new ArrayList<>(wristQueue);
                        if (watchList.size() > watchDataNum) {
                            Collections.reverse(watchList);
                            for (int i = 0; i < watchDataNum; i++) {
                                accelData[i] = watchList.get(watchDataNum - 1 - i);
                            }
                            final String watchName = LinkDataManager.getInstance().getUwbCode_wristbandName().get(next.getCode());
                            Log.i("fly_match_watch---" + watchName, gson.toJson(new ArrayList<>(Arrays.asList(accelData))));
                            Log.i("fly_match_sizeOfWatch--" + watchName, accelData.length + "");
                            int watchSeqNum = (second / 5) * 6;
                            int[] xxx = new int[watchSeqNum];
                            LimitQueue<Integer> watchSeq = wristbandProcessor.getWatchSeq();
                            if (watchSeq.size() >= watchSeqNum) {
                                List<Integer> integerList = new ArrayList<>(watchSeq);
                                for (int i = 0; i < watchSeqNum; i++) {
                                    xxx[i] = integerList.get(integerList.size() - watchSeqNum + i);
                                }
                                Log.i("fly_match_watchSeq---" + watchName, Arrays.toString(xxx));

                            }
                            boolean errorWatch = false;
                            for (int i = 0; i < xxx.length; i++) {
                                if (i != xxx.length - 1) {
                                    if ((xxx[i + 1] - xxx[i]) != 1 && (xxx[i + 1] - xxx[i]) != -65535) {
                                        errorWatch = true;
                                    }
                                }
                            }
                            if (errorWatch) {
                                Log.i("fly_match_stateOfWatch", watchName + "数据异常");
                            } else {
                                Log.i("fly_match_stateOfWatch", watchName + "数据正常");

                            }


                            watchData.setData(accelData);
                            int matchNum = NDKTools.match_data(deviceData, (short) deviceData.length, watchData, (short) watchDataNum);

                            byte[] bytes = CalculateUtil.intToByteArray(matchNum);
                            Log.i("fly_match_two---" + watchName, String.valueOf(CalculateUtil.byteToInt(bytes[2])));
                            Log.i("fly_match_three---" + watchName, String.valueOf(CalculateUtil.byteToInt(bytes[3])));
                            Log.i("fly_match_result---" + watchName, matchNum + "");

                            MatchResult matchResult = new MatchResult();
                            matchResult.setDeviceStatus(error);
                            matchResult.setWatchStatus(errorWatch);
                            matchResult.setDeviceName(deviceByBleName.getDeviceName());
                            String name = LinkDataManager.getInstance().getUwbCode_wristbandName().get(next.getCode());
                            matchResult.setWristband(name == null ? "" : name);
                            matchResult.setMatch_time(String.valueOf(second));
                            matchResult.setMatch_two(String.valueOf(CalculateUtil.byteToInt(bytes[2])));
                            matchResult.setMatch_three(String.valueOf(CalculateUtil.byteToInt(bytes[3])));
                            EventBus.getDefault().post(matchResult);
                        }
                    }
                }
            }
        }
*/


        if (select && System.currentTimeMillis() - startTime >= 10 * 1000) {
            ConcurrentHashMap<String, UwbQueue<Point>> spareTire = LinkDataManager.getInstance().queryQueueByDeviceId(deviceByBleName.getId());
            if (spareTire != null && !spareTire.isEmpty()) {

                Log.i("pppppppp", "-5-5-5");
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
        }

        if (!FinalDataManager.getInstance().alreadyBind(deviceByBleName.getFencePoint().getFenceId())) {
            Log.i("ppppppp", CalculateUtil.byteToInt(serviceData[13]) + "");
            if (CalculateUtil.byteToInt(serviceData[13]) > 0) {
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
                //   devicesList.add(serviceData[j]);
            }
            //  deviceSeq.offer(CalculateUtil.byteArrayToInt(seqNum));
        }


        if (serviceData[0] == -1 && serviceData[1] == -1) {
            start = true;
            select = true;
            flag = CalculateUtil.byteArrayToInt(seqNum);
            devicesList.clear();
            deviceSeq.clear();
            FinalDataManager.getInstance().getMatchTemp().clear();
            int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
            FinalDataManager.getInstance().getAlternative().remove(fenceId);

            if (serviceData[10] == 0 || serviceData[13] == 0) {
                //       deviceByBleName.setAbility(0);
                return null;
            }
            byte act_time = serviceData[13];
            byte gravity = serviceData[10];


            float actualGravity = 0;
            if (gravity > 0) {
                LinkBLE linkBLE = LinkDataManager.getInstance().queryLinkBle(deviceByBleName, bleName);
                if (linkBLE != null) {
                    float[] weight = linkBLE.getWeight();
                    actualGravity = weight[gravity - 1];
                }
                Log.i("zhiliang", actualGravity + "");
            }

            byte u_time = serviceData[14];

            if (bleDeviceInfoNow != null) {
                bleDeviceInfoNow.setDevice_name(deviceByBleName.getDeviceName());
                bleDeviceInfoNow.setGravity(String.valueOf(actualGravity));
                bleDeviceInfoNow.setTime(String.valueOf(act_time));
                bleDeviceInfoNow.setU_time(String.valueOf(CalculateUtil.byteToInt(u_time)));
                bleDeviceInfoNow.setSeq_num(String.valueOf(CalculateUtil.byteArrayToInt(seqNum)));
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

            DevicePower.DataBean dataBean = new DevicePower.DataBean();
            dataBean.setSerial_no(String.valueOf(1));
            dataBean.setDevice_id(bleName);
            dataBean.setDevice(deviceByBleName.getDeviceName());
            int powerLevel = CalculateUtil.byteToInt(serviceData[15]);
            dataBean.setBattery(String.valueOf(100 / powerLevel));
            FinalDataManager.getInstance().getBleName_dateBean().put(bleName, dataBean);
            return true;
        }
        return false;
    }

    private void uploadFlagStatus(int seq, int flag, LinkSpecificDevice deviceByBleName, String bleName) {
        FlagStatus flagStatus = new FlagStatus();
        flagStatus.setSeq(seq);
        flagStatus.setFlag(flag);
        flagStatus.setBleName(bleName);
        flagStatus.setDeviceName(deviceByBleName.getDeviceName());
        flagStatus.setGymName(BuildConfig.GYM_NAME);

        flagStatus.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {

                Log.i("99999-----", s == null ? "null" : s);
                Log.i("99999eeeee", e == null ? "null" : e.getMessage());
            }
        });
    }
}
