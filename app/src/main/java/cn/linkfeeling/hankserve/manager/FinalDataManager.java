package cn.linkfeeling.hankserve.manager;

import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.UWBCoordData;


/**
 * @author create by zhangyong
 * @time 2019/5/10
 */
public class FinalDataManager {

    private static final FinalDataManager finalDataManager = new FinalDataManager();


    private ConcurrentHashMap<String, BleDeviceInfo> wristbands;
    private ConcurrentHashMap<Integer, UWBCoordData> fenceId_uwbData;


    private FinalDataManager() {

    }


    public void initObject() {
        wristbands = new ConcurrentHashMap<>();  //手环对应的集合   key为手环名称   value为整合后的数据（最终上传数据）
        fenceId_uwbData = new ConcurrentHashMap<>();//围栏id uwb设备对应关系  key为围栏id  value为uwb对象
    }


    public static FinalDataManager getInstance() {
        return finalDataManager;
    }

    public ConcurrentHashMap<String, BleDeviceInfo> getWristbands() {
        return wristbands;
    }

    public ConcurrentHashMap<Integer, UWBCoordData> getFenceId_uwbData() {
        return fenceId_uwbData;
    }

    /**
     * 根据蓝牙名字判断当前围栏是否已经绑定uwb
     *
     * @param bleName
     * @return
     */
    public boolean containFenceId(String bleName) {
        int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
        return fenceId_uwbData.containsKey(fenceId);
    }


    /**
     * 判断围栏范围是否包含uwb并且手环
     * @param bleName
     * @return
     */
    public BleDeviceInfo containUwbAndWristband(String bleName) {
        if (!containFenceId(bleName)) {
            return null;
        }

        int fenceIdByBleName = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
        if (fenceIdByBleName == -1) {
            return null;
        }
        UWBCoordData uwbCoordData = FinalDataManager.getInstance().getFenceId_uwbData().get(fenceIdByBleName);
        if (uwbCoordData != null && uwbCoordData.getWristband() != null) {
            String bracelet_id = uwbCoordData.getWristband().getBracelet_id();
            return FinalDataManager.getInstance().getWristbands().get(bracelet_id);

        }
        return null;

    }
}
