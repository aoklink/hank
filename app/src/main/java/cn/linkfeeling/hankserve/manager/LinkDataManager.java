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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkBLE;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.bean.Wristband;

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
    public static final String BIRD_1 = "砝码器械t4";


    private static final LinkDataManager linkDataManager = new LinkDataManager();
    private ConcurrentHashMap<String, String> deviceBleTypeMaps;
    private ConcurrentHashMap<String, String> uwbCode_wristbandName;
    private List<LinkSpecificDevice> devicesData = new ArrayList<>();
    private Gson gson = new Gson();


    private LinkDataManager() {
        deviceBleTypeMaps = new ConcurrentHashMap<>();
        uwbCode_wristbandName = new ConcurrentHashMap<>();
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
                bufferedReader.close();
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


    public LinkSpecificDevice queryDeviceByName(String deviceName) {
        List<LinkSpecificDevice> devicesData = LinkDataManager.getInstance().devicesData;
        for (LinkSpecificDevice devicesDatum : devicesData) {
            if (devicesDatum.getDeviceName().equals(deviceName)) {
                return devicesDatum;
            }
        }
        return null;
    }
}
