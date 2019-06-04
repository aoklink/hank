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
}
