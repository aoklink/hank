package cn.linkfeeling.hankserve.manager;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.DevicePower;
import cn.linkfeeling.hankserve.bean.Point;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.queue.UwbQueue;


/**
 * @author create by zhangyong
 * @time 2019/5/10
 */
public class FinalDataManager {

    private static final FinalDataManager finalDataManager = new FinalDataManager();


    private ConcurrentHashMap<String, String> rssi_wristbands;
    private ConcurrentHashMap<String, BleDeviceInfo> wristbands;
    private ConcurrentHashMap<Integer, UWBCoordData> fenceId_uwbData;
    private ConcurrentHashMap<String, UwbQueue<Point>> code_points;
    private ConcurrentHashMap<String, DevicePower.DataBean> bleName_dateBean;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> alternative;
    private ConcurrentHashMap<Integer, ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> matchTemp;

    private FinalDataManager() {

    }


    public void initObject() {
        rssi_wristbands = new ConcurrentHashMap<>();
        wristbands = new ConcurrentHashMap<>();  //手环对应的集合   key为手环名称   value为整合后的数据（最终上传数据）
        fenceId_uwbData = new ConcurrentHashMap<>();//围栏id uwb设备对应关系  key为围栏id  value为uwb对象
        code_points = new ConcurrentHashMap<>();  //
        alternative = new ConcurrentHashMap<>(); //备选池子
        matchTemp = new ConcurrentHashMap<>(); //临时的  可被删除
        bleName_dateBean=new ConcurrentHashMap<>();//存储设备电量
    }

    public ConcurrentHashMap<String, DevicePower.DataBean> getBleName_dateBean() {
        return bleName_dateBean;
    }

    public ConcurrentHashMap<String, String> getRssi_wristbands() {
        return rssi_wristbands;
    }

    public static FinalDataManager getInstance() {
        return finalDataManager;
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> getMatchTemp() {
        return matchTemp;
    }

    public ConcurrentHashMap<String, BleDeviceInfo> getWristbands() {
        return wristbands;
    }

    public ConcurrentHashMap<Integer, UWBCoordData> getFenceId_uwbData() {
        return fenceId_uwbData;
    }

    public ConcurrentHashMap<String, UwbQueue<Point>> getCode_points() {
        return code_points;
    }

    public ConcurrentHashMap<Integer, ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> getAlternative() {
        return alternative;
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
     *
     * @param bleName
     * @return
     */
    public BleDeviceInfo containUwbAndWristband(String bleName) {
        int fenceIdByBleName = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
        if (fenceIdByBleName == -1) {
            Log.i("00000000000fence", "null");
            return null;
        }
        UWBCoordData uwbCoordData1 = FinalDataManager.getInstance().getFenceId_uwbData().get(fenceIdByBleName);
        if (uwbCoordData1 == null) {
            Log.i("00000000000uwbCoordData", "null");
            return null;
        }
        String code = uwbCoordData1.getCode();

        Log.i("00000000000code", code + "");
        String bracelet_id = LinkDataManager.getInstance().getUwbCode_wristbandName().get(code);
        Log.i("00000000000bracelet_id", bracelet_id + "");
        if (bracelet_id != null) {

            return FinalDataManager.getInstance().getWristbands().get(bracelet_id);
        }
        return null;

    }


    /**
     * 根据uwb code查询在围栏内的uwb
     *
     * @param code
     * @return
     */
    public UWBCoordData queryUwb(String code) {

        Iterator iterator = fenceId_uwbData.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, UWBCoordData> entry = (Map.Entry) iterator.next();
            UWBCoordData value = entry.getValue();
            if (code.equals(value.getCode())) {
                return value;
            }
        }
        return null;
    }


    /**
     * 根据uwb code查询备胎的uwb
     *
     * @param code
     * @return
     */
    public List<UWBCoordData> querySpareFireUwb(String code) {

        List<UWBCoordData> list = new ArrayList<>();

        Collection<ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> values = alternative.values();
        for (ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> value : values) {
            for (Map.Entry<UWBCoordData, UwbQueue<Point>> next : value.entrySet()) {
                if (next.getKey().getCode().equals(code)) {
                    list.add(next.getKey());
                }
            }
        }
        return list;
    }

    /**
     * 移除uwb
     */
    public void removeSpareFireUwb(UWBCoordData uwbCoordData) {
        Collection<ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> values = alternative.values();
        Iterator<ConcurrentHashMap<UWBCoordData, UwbQueue<Point>>> iterator = values.iterator();
        while (iterator.hasNext()) {
            ConcurrentHashMap<UWBCoordData, UwbQueue<Point>> next = iterator.next();
            Iterator<Map.Entry<UWBCoordData, UwbQueue<Point>>> iterator1 = next.entrySet().iterator();
            while (iterator1.hasNext()) {
                Map.Entry<UWBCoordData, UwbQueue<Point>> next1 = iterator1.next();
                if (next1.getKey().getCode().equals(uwbCoordData.getCode())) {
                    iterator1.remove();
                }
            }
        }
    }


    /**
     * 移除uwb
     *
     * @param fenceId
     */
    public void removeUwb(int fenceId) {
        String anchName = LinkDataManager.getInstance().getAnchName(fenceId);
        if (anchName != null) {
            if (FinalDataManager.getInstance().getRssi_wristbands().containsKey(anchName)) {
                FinalDataManager.getInstance().getRssi_wristbands().remove(anchName);
            }
        }
        if (fenceId_uwbData.containsKey(fenceId)) {
            fenceId_uwbData.remove(fenceId);
        }
    }


    /**
     * 移除rssi缓存数据
     *
     * @param anch
     */
    public void removeRssi(String anch) {
        if (rssi_wristbands.containsKey(anch)) {
            rssi_wristbands.remove(anch);
        }
    }


    /**
     * 器械是否已经绑定手环
     *
     * @param fenceId
     * @return
     */
    public boolean alreadyBind(int fenceId) {
        if (fenceId_uwbData.containsKey(fenceId)) {
            return true;

        }
        return false;
    }

    /**
     * 器械是否已经绑定手环
     *
     * @param uwbCode
     * @return
     */
    public boolean alreadyBind(String uwbCode) {
        Collection<UWBCoordData> values = fenceId_uwbData.values();
        for (UWBCoordData value : values) {
            if (uwbCode.equals(value.getCode())) {
                return true;
            }
        }
        return false;
    }

}
