package cn.linkfeeling.hankserve.manager;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.FinalBind;
import cn.linkfeeling.hankserve.bean.LinkBLE;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.Wristband;
import cn.linkfeeling.hankserve.queue.UwbQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;


/**
 * @author create by zhangyong
 * @time 2019/3/14
 */
public class LinkDataManager {
    private static final int ExpandRange_100 = 0;
    private static final int ExpandRange_20 = 0;
    private static final String FIRST_LEVEL = "json";
    private static final String SECOND_LEVEL = BuildConfig.PROJECT_NAME;
    private static final String SUFFIX = ".json";


    public static final String TYPE_LEAP = "LEAP";
    public static final String TREADMILL_1 = "跑步机t1";
    public static final String BICYCLE_1 = "单车t2";
    public static final String OVAL_1 = "椭圆机t3";
    public static final String BIRD_1 = "砝码器械t4";
    public static final String ANCH = "ANCH";


    private static final LinkDataManager linkDataManager = new LinkDataManager();
    private ConcurrentHashMap<String, String> deviceBleTypeMaps;
    private ConcurrentHashMap<String, String> uwbCode_wristbandName;
    private ConcurrentHashMap<String, Integer> wristPowerMap;
    private List<LinkSpecificDevice> devicesData = new ArrayList<>();
    private Gson gson = new Gson();


    private LinkDataManager() {
        deviceBleTypeMaps = new ConcurrentHashMap<>();
        uwbCode_wristbandName = new ConcurrentHashMap<>();
        wristPowerMap = new ConcurrentHashMap<>();
    }
    public ConcurrentHashMap<String, Integer> getWristPowerMap() {
        return wristPowerMap;
    }


    public static LinkDataManager getInstance() {
        return linkDataManager;
    }


    public LinkDataManager createLinkData(Context context) {
        BufferedReader bufferedReader = null;
        StringBuilder rewardJson = new StringBuilder();
        String rewardJsonLine;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(FIRST_LEVEL + "/" + SECOND_LEVEL + SUFFIX)));
            while ((rewardJsonLine = bufferedReader.readLine()) != null) {
                rewardJson.append(rewardJsonLine);
            }
            JSONObject jsonObject = new JSONObject(rewardJson.toString());
            JSONObject uwb_wristband = (JSONObject) jsonObject.get("uwb_wristband");
            JSONArray deviceInfo = (JSONArray) jsonObject.get("deviceInfo");

            ConcurrentHashMap<String, String> map = gson.fromJson(uwb_wristband.toString(), ConcurrentHashMap.class);
            if (map != null) {
                uwbCode_wristbandName = map;
            }


            List<LinkSpecificDevice> linkSpecificDevices = gson.fromJson(deviceInfo.toString(), new TypeToken<List<LinkSpecificDevice>>() {
            }.getType());
            if (linkSpecificDevices != null) {
                devicesData.clear();
                devicesData.addAll(linkSpecificDevices);
            }

            Log.i("5555555", gson.toJson(deviceInfo));

            for (LinkSpecificDevice devicesDatum : devicesData) {
                List<LinkBLE> linkBLES = devicesDatum.getLinkBLES();
                if (linkBLES != null && !linkBLES.isEmpty()) {
                    for (LinkBLE linkBLE : linkBLES) {
                        deviceBleTypeMaps.put(linkBLE.getBleName(), linkBLE.getType());
                    }
                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return this;
    }

    public List<LinkSpecificDevice> getDevicesData() {
        return devicesData;
    }


    public ConcurrentHashMap<String, String> getDeviceBleTypeMaps() {
        return deviceBleTypeMaps;
    }

    public ConcurrentHashMap<String, String> getUwbCode_wristbandName() {
        return uwbCode_wristbandName;
    }

    public UWBCoordData fenceIdAndUwbHasContact(Map<Integer, UWBCoordData> map01, String bleName) {
        int fenceId = -1;
        //是否是后台录入的设备
        if (map01 == null) {
            return null;
        }
        for (LinkSpecificDevice devicesDatum : devicesData) {
            List<LinkBLE> linkBLES = devicesDatum.getLinkBLES();
            if (linkBLES != null) {
                for (LinkBLE linkBLE : linkBLES) {
                    if (linkBLE.getBleName().equals(bleName)) {
                        fenceId = devicesDatum.getFencePoint().getFenceId();
                        break;
                    }
                }
            }
        }
        if (fenceId == -1) {
            return null;
        }

        for (Map.Entry<Integer, UWBCoordData> entry : map01.entrySet()) {
            if (fenceId == entry.getKey()) {
                return entry.getValue();
            }
        }
        return null;
    }

    public int getFenceIdByBleName(String bleName) {
        for (LinkSpecificDevice devicesDatum : devicesData) {
            List<LinkBLE> linkBLES = devicesDatum.getLinkBLES();
            if (linkBLES != null) {
                for (LinkBLE linkBLE : linkBLES) {
                    if (bleName.equals(linkBLE.getBleName())) {
                        return devicesDatum.getFencePoint().getFenceId();
                    }
                }
            }
        }

        return -1;


    }


    public LinkSpecificDevice getDeviceByBleName(String bleName) {
        for (LinkSpecificDevice devicesDatum : devicesData) {
            List<LinkBLE> linkBLES = devicesDatum.getLinkBLES();
            if (linkBLES != null) {
                for (LinkBLE linkBLE : linkBLES) {
                    if (bleName.equals(linkBLE.getBleName())) {
                        return devicesDatum;
                    }
                }
            }
        }
        return null;
    }


    /**
     * 初始化上传数据结构
     *
     * @param bleDeviceInfo
     */
    public void initBleDeviceInfo(BleDeviceInfo bleDeviceInfo) {
        bleDeviceInfo.setGym_name(BuildConfig.GYM_NAME);
        bleDeviceInfo.setExercise_time(String.valueOf(INTERVAL_TIME));
        bleDeviceInfo.setDevice_name("");
        bleDeviceInfo.setSpeed("");
        bleDeviceInfo.setGradient("");
        bleDeviceInfo.setDistance("");
        bleDeviceInfo.setGravity("");
        bleDeviceInfo.setTime("");
        bleDeviceInfo.setU_time("");
        bleDeviceInfo.setCurve(Collections.synchronizedList(new ArrayList<>()));
    }


    /**
     * 初始化上传数据结构
     *
     * @param bleDeviceInfo
     */
    public void cleanBleDeviceInfo(BleDeviceInfo bleDeviceInfo) {
        bleDeviceInfo.setSpeed("");
        bleDeviceInfo.setGradient("");
        bleDeviceInfo.setDistance("");
//        bleDeviceInfo.setGravity("");
//        bleDeviceInfo.setTime("");
//        bleDeviceInfo.setGravity("");
//        bleDeviceInfo.setTime("");
//        bleDeviceInfo.setU_time("");
    }

    /**
     * 清除飞鸟的数据
     *
     * @param bleDeviceInfo
     */
    public void cleanFlyBird(BleDeviceInfo bleDeviceInfo) {
        bleDeviceInfo.setGravity("");
        bleDeviceInfo.setTime("");
        bleDeviceInfo.setU_time("");
    }


    public boolean withinTheScope(UWBCoordData uwbCoorData) {

        double x = uwbCoorData.getX();
        double y = uwbCoorData.getY();

        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().getDevicesData();
        for (LinkSpecificDevice devicesDatum : devicesData) {
            UWBCoordData.FencePoint fencePoint = devicesDatum.getFencePoint();
            double x1 = fencePoint.getLeft_top().getX();
            double y1 = fencePoint.getLeft_top().getY();
            double x2 = fencePoint.getRight_bottom().getX();
            double y2 = fencePoint.getRight_bottom().getY();

            if ((x > x1 && x < x2) && (y > y1 && y < y2)) {
                uwbCoorData.setDevice(devicesDatum);
                uwbCoorData.setWristband(new Wristband(LinkDataManager.getInstance().getUwbCode_wristbandName().get(uwbCoorData.getCode())));
                return true;
            }
        }
        return false;
    }


    /**
     * 判断一个点是否在凸四边形内
     *
     * @param uwbCoordData
     * @return
     */
    public synchronized boolean isPointInRect(UWBCoordData uwbCoordData) {
        double x = uwbCoordData.getX();
        double y = uwbCoordData.getY();
        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().getDevicesData();
        try {
            for (LinkSpecificDevice devicesDatum : devicesData) {
                UWBCoordData.FencePoint fencePoint = devicesDatum.getFencePoint();
                UWBCoordData.FencePoint.Point A = fencePoint.getRight_top();
                UWBCoordData.FencePoint.Point B = fencePoint.getLeft_top();
                UWBCoordData.FencePoint.Point C = fencePoint.getLeft_bottom();
                UWBCoordData.FencePoint.Point D = fencePoint.getRight_bottom();
                if ("跑步机".equals(devicesDatum.getType()) || "椭圆机".equals(devicesDatum.getType())) {
                    final double a = ((B.x - ExpandRange_100) - (A.x + ExpandRange_100)) * (y - A.y) - (B.y - A.y) * (x - (A.x + ExpandRange_100));
                    final double b = ((C.x - ExpandRange_100) - (B.x - ExpandRange_100)) * (y - B.y) - (C.y - B.y) * (x - (B.x - ExpandRange_100));
                    final double c = ((D.x + ExpandRange_100) - (C.x - ExpandRange_100)) * (y - C.y) - (D.y - C.y) * (x - (C.x - ExpandRange_100));
                    final double d = ((A.x + ExpandRange_100) - (D.x + ExpandRange_100)) * (y - D.y) - (A.y - D.y) * (x - (D.x + ExpandRange_100));
                    if ((a > 0 && b > 0 && c > 0 && d > 0) || (a < 0 && b < 0 && c < 0 && d < 0)) {
                        uwbCoordData.setDevice(devicesDatum);
                        writeQueue(uwbCoordData);
                    }
                } else {
                    final double a = ((B.x-ExpandRange_20) - (A.x+ExpandRange_20)) * (y - (A.y-ExpandRange_20)) - ((B.y-ExpandRange_20) - (A.y-ExpandRange_20)) * (x - (A.x+ExpandRange_20));
                    final double b = ((C.x-ExpandRange_20) - (B.x-ExpandRange_20)) * (y - (B.y-ExpandRange_20)) - ((C.y+ExpandRange_20) - (B.y-ExpandRange_20)) * (x - (B.x-ExpandRange_20));
                    final double c = ((D.x+ExpandRange_20) - (C.x-ExpandRange_20)) * (y - (C.y+ExpandRange_20)) - ((D.y+ExpandRange_20) - (C.y+ExpandRange_20)) * (x - (C.x-ExpandRange_20));
                    final double d = ((A.x+ExpandRange_20) - (D.x+ExpandRange_20)) * (y - (D.y+ExpandRange_20)) - ((A.y-ExpandRange_20) - (D.y+ExpandRange_20)) * (x - (D.x+ExpandRange_20));
                    if ((a > 0 && b > 0 && c > 0 && d > 0) || (a < 0 && b < 0 && c < 0 && d < 0)) {
                        uwbCoordData.setDevice(devicesDatum);
                        writeQueue(uwbCoordData);
                    }
                }
            }
            if (uwbCoordData.getDevice() == null) {
                writeQueue(uwbCoordData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (LinkSpecificDevice devicesDatum : devicesData) {
            UWBCoordData.FencePoint fencePoint = devicesDatum.getFencePoint();
            UWBCoordData.FencePoint.Point A = fencePoint.getRight_top();
            UWBCoordData.FencePoint.Point B = fencePoint.getLeft_top();
            UWBCoordData.FencePoint.Point C = fencePoint.getLeft_bottom();
            UWBCoordData.FencePoint.Point D = fencePoint.getRight_bottom();
            final double a = (B.x - A.x) * (y - A.y) - (B.y - A.y) * (x - A.x);
            final double b = (C.x - B.x) * (y - B.y) - (C.y - B.y) * (x - B.x);
            final double c = (D.x - C.x) * (y - C.y) - (D.y - C.y) * (x - C.x);
            final double d = (A.x - D.x) * (y - D.y) - (A.y - D.y) * (x - D.x);
            if ((a > 0 && b > 0 && c > 0 && d > 0) || (a < 0 && b < 0 && c < 0 && d < 0)) {
                uwbCoordData.setDevice(devicesDatum);
                uwbCoordData.setWristband(new Wristband(LinkDataManager.getInstance().getUwbCode_wristbandName().get(uwbCoordData.getCode())));
                return true;
            }
        }
        uwbCoordData.setDevice(null);
        uwbCoordData.setWristband(new Wristband(LinkDataManager.getInstance().getUwbCode_wristbandName().get(uwbCoordData.getCode())));
        return false;
    }

    private void writeQueue(UWBCoordData uwbCoordData) {
        UwbQueue<Point> points = FinalDataManager.getInstance().getCode_points().get(uwbCoordData.getCode());
        if (points == null) {
            UwbQueue<Point> uwbQueue = new UwbQueue<>(50);
            Point point = new Point();
            if (uwbCoordData.getDevice() == null) {
                point.setId(-1);
            } else {
                point.setId(uwbCoordData.getDevice().getId());
            }
            point.setX(uwbCoordData.getX());
            point.setY(uwbCoordData.getY());
            uwbQueue.offer(point);
            FinalDataManager.getInstance().getCode_points().put(uwbCoordData.getCode(), uwbQueue);
        } else {
            Point point = new Point();
            if (uwbCoordData.getDevice() == null) {
                point.setId(-1);
            } else {
                point.setId(uwbCoordData.getDevice().getId());
            }
            point.setX(uwbCoordData.getX());
            point.setY(uwbCoordData.getY());
            points.offer(point);
        }
    }


    public boolean contain(UWBCoordData old, UWBCoordData newU) {
        if (old.getDevice() != null && old.getDevice().getFencePoint() != null) {
            UWBCoordData.FencePoint fencePoint = old.getDevice().getFencePoint();
            UWBCoordData.FencePoint.Point A = fencePoint.getRight_top();
            UWBCoordData.FencePoint.Point B = fencePoint.getLeft_top();
            UWBCoordData.FencePoint.Point C = fencePoint.getLeft_bottom();
            UWBCoordData.FencePoint.Point D = fencePoint.getRight_bottom();
            double x = newU.getX();
            double y = newU.getY();
            if ("跑步机".equals(old.getDevice().getType()) || "椭圆机".equals(old.getDevice().getType())) {
                final double a = ((B.x - ExpandRange_100) - (A.x + ExpandRange_100)) * (y - A.y) - (B.y - A.y) * (x - (A.x + ExpandRange_100));
                final double b = ((C.x - ExpandRange_100) - (B.x - ExpandRange_100)) * (y - B.y) - (C.y - B.y) * (x - (B.x - ExpandRange_100));
                final double c = ((D.x + ExpandRange_100) - (C.x - ExpandRange_100)) * (y - C.y) - (D.y - C.y) * (x - (C.x - ExpandRange_100));
                final double d = ((A.x + ExpandRange_100) - (D.x + ExpandRange_100)) * (y - D.y) - (A.y - D.y) * (x - (D.x + ExpandRange_100));
                if ((a > 0 && b > 0 && c > 0 && d > 0) || (a < 0 && b < 0 && c < 0 && d < 0)) {
                    return true;
                }
            } else {
                final double a = ((B.x-ExpandRange_20) - (A.x+ExpandRange_20)) * (y - (A.y-ExpandRange_20)) - ((B.y-ExpandRange_20) - (A.y-ExpandRange_20)) * (x - (A.x+ExpandRange_20));
                final double b = ((C.x-ExpandRange_20) - (B.x-ExpandRange_20)) * (y - (B.y-ExpandRange_20)) - ((C.y+ExpandRange_20) - (B.y-ExpandRange_20)) * (x - (B.x-ExpandRange_20));
                final double c = ((D.x+ExpandRange_20) - (C.x-ExpandRange_20)) * (y - (C.y+ExpandRange_20)) - ((D.y+ExpandRange_20) - (C.y+ExpandRange_20)) * (x - (C.x-ExpandRange_20));
                final double d = ((A.x+ExpandRange_20) - (D.x+ExpandRange_20)) * (y - (D.y+ExpandRange_20)) - ((A.y-ExpandRange_20) - (D.y+ExpandRange_20)) * (x - (D.x+ExpandRange_20));
                if ((a > 0 && b > 0 && c > 0 && d > 0) || (a < 0 && b < 0 && c < 0 && d < 0)) {
                    return true;
                }
            }

        }
        return false;
    }


    /**
     * 根据蓝牙名字查询对象
     *
     * @param bleName
     * @return
     */
    public LinkBLE queryLinkBle(LinkSpecificDevice specificDevice, String bleName) {
        List<LinkBLE> linkBLES = specificDevice.getLinkBLES();
        if (linkBLES != null && !linkBLES.isEmpty()) {
            for (LinkBLE linkBLE : linkBLES) {
                if (linkBLE.getBleName().equals(bleName)) {
                    return linkBLE;
                }
            }
        }
        return null;
    }

    /**
     * 根据anchName 获取设备
     *
     * @param anchName
     * @return
     */
    public LinkSpecificDevice getDeviceByanchName(String anchName) {
        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (anchName.equals(devicesDatum.getAnchName())) {
                return devicesDatum;
            }
        }
        return null;
    }


    public LinkSpecificDevice queryDeviceByName(String deviceName) {
        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().devicesData;
        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (devicesDatum.getDeviceName().equals(deviceName)) {
                return devicesDatum;
            }
        }
        return null;
    }

    /**
     * g根据围栏id查找anch
     *
     * @param fenceId
     * @return
     */
    public String getAnchName(int fenceId) {
        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (devicesDatum.getFencePoint().getFenceId() == fenceId) {
                return devicesDatum.getAnchName();
            }
        }

        return null;

    }


    public ConcurrentHashMap<String, UwbQueue<Point>> queryQueueByDeviceId(int deviceId) {
        Point point;
        ConcurrentHashMap<String, UwbQueue<Point>> newCaculate = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, UwbQueue<Point>> code_points = FinalDataManager.getInstance().getCode_points();
        for (Map.Entry<String, UwbQueue<Point>> next : code_points.entrySet()) {
            UwbQueue<Point> value = next.getValue();
            point = new Point();
            point.setId(deviceId);
            String key = next.getKey();
            if (value.contains(point) && FinalDataManager.getInstance().queryUwb(next.getKey()) == null) {
                newCaculate.put(key, value);
            }
        }
        return newCaculate;
    }


    public String queryUWBCodeByWristband(String wristband) {
        Iterator<Map.Entry<String, String>> iterator = uwbCode_wristbandName.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            if (next.getValue().equals(wristband)) {
                return next.getKey();
            }
        }
        return null;
    }

    /**
     * 根据BLE扫描的RSSI 选择手环绑定
     *
     * @param uwbCode
     * @param deviceByBleName
     */
    public void bleBindAndRemoveSpareTire(String uwbCode, LinkSpecificDevice deviceByBleName) {
        UWBCoordData uwbCoordData = new UWBCoordData();
        uwbCoordData.setDevice(deviceByBleName);
        uwbCoordData.setSemaphore(0);
        uwbCoordData.setCode(uwbCode);
        FinalDataManager.getInstance().getFenceId_uwbData().put(deviceByBleName.getFencePoint().getFenceId(), uwbCoordData);
        Log.i("binding", "BLE RSSI");
        ConcurrentHashMap<Integer, ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> alternative = FinalDataManager.getInstance().getAlternative();
        if (alternative != null && !alternative.isEmpty()) {
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> queueConcurrentHashMap = alternative.get(deviceByBleName.getFencePoint().getFenceId());
            if (queueConcurrentHashMap != null && !queueConcurrentHashMap.isEmpty()) {
                Iterator<Map.Entry<UWBCoordData, UwbQueue<Point>>> iterator = queueConcurrentHashMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<UWBCoordData, UwbQueue<Point>> next = iterator.next();
                    if (next.getKey().getCode().equals(uwbCode)) {
                        iterator.remove();
                    }
                }
            }
        }

    }


    public void checkBind(LinkSpecificDevice deviceByBleName) {
        Log.i("ppppppppsizetop", FinalDataManager.getInstance().getFenceId_uwbData().size() + "");
        //围栏设备在运动
        Log.i("pppppppp", "进来了");
        //获取备选人的集合
        ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> queue = FinalDataManager.getInstance().getAlternative().get(deviceByBleName.getFencePoint().getFenceId());


        if (queue == null || queue.isEmpty()) {
            //可以理解成没有符合条件的人   不进行绑定
            Log.i("pppppppp", "-3-3-3");
            return;
        }

        //进行二次筛选    在备胎中移除所有已经绑定的标签

        Iterator<UWBCoordData> iterator = queue.keySet().iterator();
        while (iterator.hasNext()) {
            UWBCoordData next = iterator.next();
            if (FinalDataManager.getInstance().getFenceId_uwbData().containsValue(next)) {
                iterator.remove();
            }
        }

        Log.i("pppppppp2222", queue.size() + "");
        UWBCoordData uwbCoordData = null;
        float min = Integer.MAX_VALUE;
        for (Map.Entry<UWBCoordData, UwbQueue<Point>> next : queue.entrySet()) {

            int num = 0;
            UwbQueue<Point> value = next.getValue();
            UWBCoordData.FencePoint.Point centerPoint = deviceByBleName.getCenterPoint();
            for (Point point : value) {
                num += CalculateUtil.pointDistance(point.getX(), point.getY(), centerPoint.getX(), centerPoint.getY());
            }
            float v = CalculateUtil.txFloat(num, value.size());
            if (v < min) {
                min = v;
                uwbCoordData = next.getKey();
            }
        }

        FinalDataManager.getInstance().getFenceId_uwbData().put(deviceByBleName.getFencePoint().getFenceId(), uwbCoordData);
        queue.remove(uwbCoordData); //从备选人中移除

        if(uwbCoordData!=null){
            FinalBind finalBind=new FinalBind();
            finalBind.setDeviceName(deviceByBleName.getDeviceName());
            finalBind.setGymName(BuildConfig.GYM_NAME);
            finalBind.setUwbCode(uwbCoordData.getCode());
            finalBind.setWatchName(LinkDataManager.getInstance().uwbCode_wristbandName.get(uwbCoordData.getCode()));
            finalBind.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                }
            });
        }



        Log.i("binding", "UWB SCAN");
        Log.i("ppppppppsizebottom", FinalDataManager.getInstance().getFenceId_uwbData().size() + "");

        //找出带匹配手环
        //  ConcurrentHashMap<Integer, List<String>> map = queryWristByFenceId(newUwb.getDevice().getId());
        //      FinalDataManager.getInstance().getCode_points().get

    }

    public LinkSpecificDevice queryDeviceNameByFenceId(int fenceId) {

        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (devicesDatum.getFencePoint().getFenceId() == fenceId) {
                return devicesDatum;

            }
        }
        return null;

    }

    public int queryFenceIdByDeviceName(String deviceName) {
        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (deviceName.equals(devicesDatum.getDeviceName())) {
                return devicesDatum.getFencePoint().getFenceId();

            }
        }
        return -1;
    }


}
