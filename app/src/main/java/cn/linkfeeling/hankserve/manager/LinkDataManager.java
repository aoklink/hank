package cn.linkfeeling.hankserve.manager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.link.feeling.framework.utils.data.ToastUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkBLE;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.Wristband;
import cn.linkfeeling.hankserve.queue.UwbQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.Logger;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;


/**
 * @author create by zhangyong
 * @time 2019/3/14
 */
public class LinkDataManager {
    private static final String FIRST_LEVEL = "json";
    private static final String SECOND_LEVEL = BuildConfig.PROJECT_NAME;
    private static final String SUFFIX = ".json";


    public static final String TYPE_LEAP = "LEAP";
    public static final String TREADMILL_1 = "跑步机t1";
    public static final String BICYCLE_1 = "单车t2";
    public static final String OVAL_1 = "椭圆机t3";
    public static final String BIRD_1 = "飞鸟左";
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

    public static LinkDataManager getInstance() {
        return linkDataManager;
    }

    public ConcurrentHashMap<String, Integer> getWristPowerMap() {
        return wristPowerMap;
    }

    public LinkDataManager createLinkData(Context context) {
//        uwbCode_wristbandName.put("0000183c", "LEAP 2FC2");
//        uwbCode_wristbandName.put("0000183d", "LEAP BFF4");
//        uwbCode_wristbandName.put("0000183f", "LEAP 0ADB");
//        uwbCode_wristbandName.put("00001e27", "LEAP 0DDA");


//        File file = new File(Environment.getExternalStorageDirectory(), "linkfeeling/linkfeeling01.json");
//        try {
//            FileInputStream fileInputStream = new FileInputStream(file);
//            byte[] bytes = new byte[fileInputStream.available()];
//            fileInputStream.read(bytes);
//            String string = new String(bytes);
//            Logger.e("09090909",string);
//        } catch (Exception e) {
//            Toast.makeText(context, "文件加载异常，请确保文件存在", Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }


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
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


//        LinkSpecificDevice linkSpecificDevice00 = new LinkSpecificDevice();
//        linkSpecificDevice00.setId(0x1100);
//        linkSpecificDevice00.setDeviceName("HIIT");
//        linkSpecificDevice00.setType("HIIT");
//
//        UWBCoordData.FencePoint fencePoint00 = new UWBCoordData.FencePoint();
//        fencePoint00.setFenceId(0x100);
//        fencePoint00.setLeft_top(new UWBCoordData.FencePoint.Point(870, 7));
//        fencePoint00.setRight_top(new UWBCoordData.FencePoint.Point(1476, 7));
//        fencePoint00.setLeft_bottom(new UWBCoordData.FencePoint.Point(870, 512));
//        fencePoint00.setRight_bottom(new UWBCoordData.FencePoint.Point(1476, 512));
//        linkSpecificDevice00.setFencePoint(fencePoint00);

//------------------------------------------------------------------------------------------------------------------------------

//        LinkSpecificDevice linkSpecificDevice01 = new LinkSpecificDevice();
//        linkSpecificDevice01.setId(0x1101);
//        linkSpecificDevice01.setDeviceName("跑步机01");
//        linkSpecificDevice01.setType("跑步机");
//
//        UWBCoordData.FencePoint fencePoint01 = new UWBCoordData.FencePoint();
//        fencePoint01.setFenceId(0x101);
//        fencePoint01.setLeft_top(new UWBCoordData.FencePoint.Point(0, 281));
//        fencePoint01.setRight_top(new UWBCoordData.FencePoint.Point(312, 281));
//        fencePoint01.setLeft_bottom(new UWBCoordData.FencePoint.Point(0, 417));
//        fencePoint01.setRight_bottom(new UWBCoordData.FencePoint.Point(312, 417));
//        linkSpecificDevice01.setFencePoint(fencePoint01);
//
//        LinkBLE linkBLE01 = new LinkBLE();
//        linkBLE01.setBleId(0x11101);
//        linkBLE01.setBleName("LKFL02");
//        linkBLE01.setType(TREADMILL_1);
//        deviceBleTypeMaps.put(linkBLE01.getBleName(), linkBLE01.getType());
//
//        List<LinkBLE> list01 = new ArrayList<>();
//        list01.add(linkBLE01);
//        linkSpecificDevice01.setLinkBLES(list01);


//--------------------------------------------------------------------------------------------------------------//
//        LinkSpecificDevice linkSpecificDevice02 = new LinkSpecificDevice();
//        linkSpecificDevice02.setId(0x1102);
//        linkSpecificDevice02.setDeviceName("单车");
//        linkSpecificDevice02.setType("单车");
//
//        UWBCoordData.FencePoint fencePoint02 = new UWBCoordData.FencePoint();
//        fencePoint02.setFenceId(0x102);
//        fencePoint02.setLeft_top(new UWBCoordData.FencePoint.Point(1, 6));
//        fencePoint02.setRight_top(new UWBCoordData.FencePoint.Point(316, 6));
//        fencePoint02.setLeft_bottom(new UWBCoordData.FencePoint.Point(1, 147));
//        fencePoint02.setRight_bottom(new UWBCoordData.FencePoint.Point(316, 147));
//        linkSpecificDevice02.setFencePoint(fencePoint02);
//
//        LinkBLE linkBLE02 = new LinkBLE();
//        linkBLE02.setBleId(0x11102);
//        linkBLE02.setBleName("LKFL03");
//        linkBLE02.setType(BICYCLE_1);
//        deviceBleTypeMaps.put(linkBLE02.getBleName(), linkBLE02.getType());
//
//        List<LinkBLE> list02 = new ArrayList<>();
//        list02.add(linkBLE02);
//        linkSpecificDevice02.setLinkBLES(list02);

        //----------------------------------------------------------------------------------------------------//

//        LinkSpecificDevice linkSpecificDevice03 = new LinkSpecificDevice();
//        linkSpecificDevice03.setId(0x1103);
//        linkSpecificDevice03.setDeviceName("椭圆机");
//        linkSpecificDevice03.setType("椭圆机");
//
//        UWBCoordData.FencePoint fencePoint03 = new UWBCoordData.FencePoint();
//        fencePoint03.setFenceId(0x103);
//        fencePoint03.setLeft_top(new UWBCoordData.FencePoint.Point(0, 158));
//        fencePoint03.setRight_top(new UWBCoordData.FencePoint.Point(357, 158));
//        fencePoint03.setLeft_bottom(new UWBCoordData.FencePoint.Point(0, 275));
//        fencePoint03.setRight_bottom(new UWBCoordData.FencePoint.Point(357, 275));
//        linkSpecificDevice03.setFencePoint(fencePoint03);
//
//        LinkBLE linkBLE03 = new LinkBLE();
//        linkBLE03.setBleId(0x11103);
//        linkBLE03.setBleName("LKFL05");
//        linkBLE03.setType(OVAL_1);
//
//        deviceBleTypeMaps.put(linkBLE03.getBleName(), linkBLE03.getType());
//
//        List<LinkBLE> list03 = new ArrayList<>();
//        list03.add(linkBLE03);
//        linkSpecificDevice03.setLinkBLES(list03);

        //--------------------------------------------------------------------------------------------------------------------

//        LinkSpecificDevice linkSpecificDevice04 = new LinkSpecificDevice();
//        linkSpecificDevice04.setId(0x1104);
//        linkSpecificDevice04.setDeviceName("飞鸟架01");
//        linkSpecificDevice04.setType("飞鸟架01");
//
//        UWBCoordData.FencePoint fencePoint04 = new UWBCoordData.FencePoint();
//        fencePoint04.setFenceId(0x104);
//        fencePoint04.setLeft_top(new UWBCoordData.FencePoint.Point(578, 9));
//        fencePoint04.setRight_top(new UWBCoordData.FencePoint.Point(860, 9));
//        fencePoint04.setLeft_bottom(new UWBCoordData.FencePoint.Point(578, 417));
//        fencePoint04.setRight_bottom(new UWBCoordData.FencePoint.Point(860, 417));
//        linkSpecificDevice04.setFencePoint(fencePoint04);
//
//        LinkBLE linkBLE04 = new LinkBLE();
//        linkBLE04.setBleId(0x11104);
//        linkBLE04.setBleName("LKFL06");
//        linkBLE04.setType(BIRD_1);
//
//        deviceBleTypeMaps.put(linkBLE04.getBleName(), linkBLE04.getType());
//
//        List<LinkBLE> list04 = new ArrayList<>();
//        list04.add(linkBLE04);
//        linkSpecificDevice04.setLinkBLES(list04);


        //------------------------------------------------------------------------------------------------------
        /*以下为测试区域*/

//        LinkSpecificDevice linkSpecificDevice05 = new LinkSpecificDevice();
//        linkSpecificDevice05.setId(0x1105);
//        linkSpecificDevice05.setDeviceName("飞鸟架02");
//        linkSpecificDevice05.setType("飞鸟架02");
//
//        UWBCoordData.FencePoint fencePoint05 = new UWBCoordData.FencePoint();
//        fencePoint05.setFenceId(0x105);
//        fencePoint05.setLeft_top(new UWBCoordData.FencePoint.Point(0, 1041));
//        fencePoint05.setRight_top(new UWBCoordData.FencePoint.Point(550, 1041));
//        fencePoint05.setLeft_bottom(new UWBCoordData.FencePoint.Point(0, 1781));
//        fencePoint05.setRight_bottom(new UWBCoordData.FencePoint.Point(550, 1781));
//        linkSpecificDevice05.setFencePoint(fencePoint05);


//=====================================================================================================
//        devicesData.add(linkSpecificDevice00);
//        devicesData.add(linkSpecificDevice01);
//        devicesData.add(linkSpecificDevice02);
//        devicesData.add(linkSpecificDevice03);
//        devicesData.add(linkSpecificDevice04);
//        devicesData.add(linkSpecificDevice05);

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
        bleDeviceInfo.setGravity("");
        bleDeviceInfo.setTime("");
        bleDeviceInfo.setU_time("");
        bleDeviceInfo.setCurve(Collections.synchronizedList(new ArrayList<>()));
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

    public LinkSpecificDevice queryDeviceByName(String deviceName) {
        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().devicesData;
        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (devicesDatum.getDeviceName().equals(deviceName)) {
                return devicesDatum;
            }
        }
        return null;
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

    /**
     * 查询所有落在该围栏内的手环（排除已经绑定的手环）
     * @param deviceId
     * @return
     */
    public ConcurrentHashMap<String, UwbQueue<Point>> queryQueueByDeviceId(int deviceId) {
        Point point;
        ConcurrentHashMap<String, UwbQueue<Point>> newCaculate = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, UwbQueue<Point>> code_points = FinalDataManager.getInstance().getCode_points();
        for (Map.Entry<String, UwbQueue<Point>> next : code_points.entrySet()) {
            UwbQueue<Point> value = next.getValue();
            point = new Point();
            point.setId(deviceId);
            String key = next.getKey();
            if (value.contains(point)
                    && FinalDataManager.getInstance().queryUwb(next.getKey()) == null
                    && FinalDataManager.getInstance().getWebAccounts().contains(LinkDataManager.getInstance().uwbCode_wristbandName.get(key))) {
                newCaculate.put(key, value);
            }
        }
        return newCaculate;
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

            UWBCoordData key = next.getKey();
            if(LinkDataManager.getInstance().excludeUwb(key)){
                return;
            }

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
        Log.i("binding", "UWB SCAN");
        Log.i("ppppppppsizebottom", FinalDataManager.getInstance().getFenceId_uwbData().size() + "");

        //找出带匹配手环
        //  ConcurrentHashMap<Integer, List<String>> map = queryWristByFenceId(newUwb.getDevice().getId());
        //      FinalDataManager.getInstance().getCode_points().get

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
        //绑定手环后从当前设备备选中移除
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

    /**
     * 判断一个点是否在凸四边形内
     *
     * @param uwbCoordData
     * @return
     */
    public boolean isPointInRect(UWBCoordData uwbCoordData) {
        double x = uwbCoordData.getX();
        double y = uwbCoordData.getY();
        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().getDevicesData();
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
        uwbCoordData.setWristband(new Wristband(LinkDataManager.getInstance().getUwbCode_wristbandName().get(uwbCoordData.getCode())));
        return false;
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

    public String queryUwbCodeByWatchName(String watchName){
        Iterator<Map.Entry<String, String>> iterator = uwbCode_wristbandName.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            if(watchName.equals(next.getValue())){
                return next.getKey();
            }
        }

        return null;

    }


    /**
     * 获取后台推送的优先级最高的绑定手环（对应相关器械）
     * @param deviceByBleName
     * @return
     */
    public BleDeviceInfo getWebFinalBind(LinkSpecificDevice deviceByBleName){
        BleDeviceInfo bleDeviceInfo=null;
        String watchName = FinalDataManager.getInstance().getDevice_wristbands().get(deviceByBleName.getDeviceName());
        if(watchName!=null){
            Log.i("333333333",watchName);
            bleDeviceInfo = FinalDataManager.getInstance().getWristbands().get(watchName);
            String uwbCode = LinkDataManager.getInstance().queryUwbCodeByWatchName(watchName);
            if(uwbCode!=null){
                ConcurrentHashMap<Integer, UWBCoordData> fenceId_uwbData = FinalDataManager.getInstance().getFenceId_uwbData();
                if(fenceId_uwbData!=null && !fenceId_uwbData.isEmpty()){
                    Iterator<Map.Entry<Integer, UWBCoordData>> iterator = fenceId_uwbData.entrySet().iterator();
                    while (iterator.hasNext()){
                        Map.Entry<Integer, UWBCoordData> next = iterator.next();
                        Integer key = next.getKey();
                        UWBCoordData value = next.getValue();
                        if(key!=deviceByBleName.getFencePoint().getFenceId() && value.getCode().equals(uwbCode)){
                            iterator.remove();
                            if(bleDeviceInfo!=null){
                                LinkDataManager.getInstance().initBleDeviceInfo(bleDeviceInfo);
                            }
                            break;
                        }
                    }
                }
            }
        }
        return bleDeviceInfo;
    }


    public boolean excludeUwb(UWBCoordData uwbCoordData){
        Collection<String> values = FinalDataManager.getInstance().getDevice_wristbands().values();
        if(!values.isEmpty()){
            for (String value : values) {
                String uwbCode = LinkDataManager.getInstance().queryUWBCodeByWristband(value);
                if(uwbCoordData.getCode().equals(uwbCode)){
                    return true;
                }
            }
        }
        return false;
    }
}
