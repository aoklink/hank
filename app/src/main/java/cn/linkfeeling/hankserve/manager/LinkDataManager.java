package cn.linkfeeling.hankserve.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkBLE;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;

import static cn.linkfeeling.hankserve.constants.LinkConstant.INTERVAL_TIME;


/**
 * @author create by zhangyong
 * @time 2019/3/14
 */
public class LinkDataManager {
    public static final String TYPE_LEAP = "LEAP";
    public static final String TREADMILL_1 = "跑步机t1";
    public static final String BICYCLE_1 = "单车t2";
    public static final String OVAL_1 = "椭圆机t3";
    public static final String BIRD_1 = "飞鸟左";

    private static final LinkDataManager linkDataManager = new LinkDataManager();
    private ConcurrentHashMap<String, String> deviceBleTypeMaps;
    private ConcurrentHashMap<String, String> uwbCode_wristbandName;
    private List<LinkSpecificDevice> devicesData = new ArrayList<>();


    private LinkDataManager() {
        deviceBleTypeMaps = new ConcurrentHashMap<>();
        uwbCode_wristbandName = new ConcurrentHashMap<>();


    }

    public static LinkDataManager getInstance() {
        return linkDataManager;
    }


    public LinkDataManager createLinkData() {
        uwbCode_wristbandName.put("00000944", "LEAP 0DDA");
        uwbCode_wristbandName.put("00000943", "LEAP 2FC2");
        uwbCode_wristbandName.put("0000183d", "LEAP BFF4");


        LinkSpecificDevice linkSpecificDevice00 = new LinkSpecificDevice();
        linkSpecificDevice00.setId(0x1100);
        linkSpecificDevice00.setDeviceName("HIIT");
        linkSpecificDevice00.setType("HIIT");

        UWBCoordData.FencePoint fencePoint00 = new UWBCoordData.FencePoint();
        fencePoint00.setFenceId(0x100);
        fencePoint00.setLeft_top(new UWBCoordData.FencePoint.Point(870, 7));
        fencePoint00.setRight_top(new UWBCoordData.FencePoint.Point(1476, 7));
        fencePoint00.setLeft_bottom(new UWBCoordData.FencePoint.Point(870, 512));
        fencePoint00.setRight_bottom(new UWBCoordData.FencePoint.Point(1476, 512));
        linkSpecificDevice00.setFencePoint(fencePoint00);

//------------------------------------------------------------------------------------------------------------------------------

        LinkSpecificDevice linkSpecificDevice01 = new LinkSpecificDevice();
        linkSpecificDevice01.setId(0x1101);
        linkSpecificDevice01.setDeviceName("跑步机01");
        linkSpecificDevice01.setType("跑步机");

        UWBCoordData.FencePoint fencePoint01 = new UWBCoordData.FencePoint();
        fencePoint01.setFenceId(0x101);
        fencePoint01.setLeft_top(new UWBCoordData.FencePoint.Point(0, 281));
        fencePoint01.setRight_top(new UWBCoordData.FencePoint.Point(312, 281));
        fencePoint01.setLeft_bottom(new UWBCoordData.FencePoint.Point(0, 417));
        fencePoint01.setRight_bottom(new UWBCoordData.FencePoint.Point(312, 417));
        linkSpecificDevice01.setFencePoint(fencePoint01);

        LinkBLE linkBLE01 = new LinkBLE();
        linkBLE01.setBleId(0x11101);
        linkBLE01.setBleName("LKFL02");
        linkBLE01.setType(TREADMILL_1);
        deviceBleTypeMaps.put(linkBLE01.getBleName(), linkBLE01.getType());

        List<LinkBLE> list01 = new ArrayList<>();
        list01.add(linkBLE01);
        linkSpecificDevice01.setLinkBLES(list01);


//--------------------------------------------------------------------------------------------------------------//
        LinkSpecificDevice linkSpecificDevice02 = new LinkSpecificDevice();
        linkSpecificDevice02.setId(0x1102);
        linkSpecificDevice02.setDeviceName("单车");
        linkSpecificDevice02.setType("单车");

        UWBCoordData.FencePoint fencePoint02 = new UWBCoordData.FencePoint();
        fencePoint02.setFenceId(0x102);
        fencePoint02.setLeft_top(new UWBCoordData.FencePoint.Point(345, 0));
        fencePoint02.setRight_top(new UWBCoordData.FencePoint.Point(530, 0));
        fencePoint02.setLeft_bottom(new UWBCoordData.FencePoint.Point(345, 240));
        fencePoint02.setRight_bottom(new UWBCoordData.FencePoint.Point(530, 240));
        linkSpecificDevice02.setFencePoint(fencePoint02);

        LinkBLE linkBLE02 = new LinkBLE();
        linkBLE02.setBleId(0x11102);
        linkBLE02.setBleName("LKFL03");
        linkBLE02.setType(BICYCLE_1);
        deviceBleTypeMaps.put(linkBLE02.getBleName(), linkBLE02.getType());

        List<LinkBLE> list02 = new ArrayList<>();
        list02.add(linkBLE02);
        linkSpecificDevice02.setLinkBLES(list02);

        //----------------------------------------------------------------------------------------------------//

        LinkSpecificDevice linkSpecificDevice03 = new LinkSpecificDevice();
        linkSpecificDevice03.setId(0x1103);
        linkSpecificDevice03.setDeviceName("椭圆机");
        linkSpecificDevice03.setType("椭圆机");

        UWBCoordData.FencePoint fencePoint03 = new UWBCoordData.FencePoint();
        fencePoint03.setFenceId(0x103);
        fencePoint03.setLeft_top(new UWBCoordData.FencePoint.Point(0, 83));
        fencePoint03.setRight_top(new UWBCoordData.FencePoint.Point(312, 83));
        fencePoint03.setLeft_bottom(new UWBCoordData.FencePoint.Point(0, 275));
        fencePoint03.setRight_bottom(new UWBCoordData.FencePoint.Point(312, 275));
        linkSpecificDevice03.setFencePoint(fencePoint03);

        LinkBLE linkBLE03 = new LinkBLE();
        linkBLE03.setBleId(0x11103);
        linkBLE03.setBleName("LKFL05");
        linkBLE03.setType(OVAL_1);

        deviceBleTypeMaps.put(linkBLE03.getBleName(), linkBLE03.getType());

        List<LinkBLE> list03 = new ArrayList<>();
        list03.add(linkBLE03);
        linkSpecificDevice03.setLinkBLES(list03);

        //--------------------------------------------------------------------------------------------------------------------

        LinkSpecificDevice linkSpecificDevice04 = new LinkSpecificDevice();
        linkSpecificDevice04.setId(0x1104);
        linkSpecificDevice04.setDeviceName("飞鸟架01");
        linkSpecificDevice04.setType("飞鸟架01");

        UWBCoordData.FencePoint fencePoint04 = new UWBCoordData.FencePoint();
        fencePoint04.setFenceId(0x104);
        fencePoint04.setLeft_top(new UWBCoordData.FencePoint.Point(578, 9));
        fencePoint04.setRight_top(new UWBCoordData.FencePoint.Point(860, 9));
        fencePoint04.setLeft_bottom(new UWBCoordData.FencePoint.Point(578, 417));
        fencePoint04.setRight_bottom(new UWBCoordData.FencePoint.Point(860, 417));
        linkSpecificDevice04.setFencePoint(fencePoint04);

        LinkBLE linkBLE04 = new LinkBLE();
        linkBLE04.setBleId(0x11104);
        linkBLE04.setBleName("LKFL");
        linkBLE04.setType(BIRD_1);

        deviceBleTypeMaps.put(linkBLE04.getBleName(), linkBLE04.getType());

        List<LinkBLE> list04 = new ArrayList<>();
        list04.add(linkBLE04);
        linkSpecificDevice04.setLinkBLES(list04);


        //------------------------------------------------------------------------------------------------------
        /*以下为测试区域*/

        LinkSpecificDevice linkSpecificDevice05 = new LinkSpecificDevice();
        linkSpecificDevice05.setId(0x1105);
        linkSpecificDevice05.setDeviceName("飞鸟架02");
        linkSpecificDevice05.setType("飞鸟架02");

        UWBCoordData.FencePoint fencePoint05 = new UWBCoordData.FencePoint();
        fencePoint05.setFenceId(0x105);
        fencePoint05.setLeft_top(new UWBCoordData.FencePoint.Point(0, 1041));
        fencePoint05.setRight_top(new UWBCoordData.FencePoint.Point(550, 1041));
        fencePoint05.setLeft_bottom(new UWBCoordData.FencePoint.Point(0, 1781));
        fencePoint05.setRight_bottom(new UWBCoordData.FencePoint.Point(550, 1781));
        linkSpecificDevice05.setFencePoint(fencePoint05);


//=====================================================================================================
        devicesData.add(linkSpecificDevice00);
        devicesData.add(linkSpecificDevice01);
        devicesData.add(linkSpecificDevice02);
        devicesData.add(linkSpecificDevice03);
        devicesData.add(linkSpecificDevice04);
        devicesData.add(linkSpecificDevice05);

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
        bleDeviceInfo.setGym_name("link_office");
        bleDeviceInfo.setExercise_time(String.valueOf(INTERVAL_TIME));
        bleDeviceInfo.setDevice_name("");
        bleDeviceInfo.setSpeed("");
        bleDeviceInfo.setGradient("");
        bleDeviceInfo.setDistance("");
        bleDeviceInfo.setGravity("");
        bleDeviceInfo.setTime("");
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
    }

    /**
     * 清除飞鸟的数据
     *
     * @param bleDeviceInfo
     */
    public void cleanFlyBird(BleDeviceInfo bleDeviceInfo) {
        bleDeviceInfo.setGravity("");
        bleDeviceInfo.setTime("");
    }


}
