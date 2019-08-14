package cn.linkfeeling.hankserve.manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
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


    private ConcurrentHashMap<String, BleDeviceInfo> wristbands;
    private ConcurrentHashMap<Integer, UWBCoordData> fenceId_uwbData;
    private ConcurrentHashMap<String, UwbQueue<Point>> code_points;


    private FinalDataManager() {

    }


    public void initObject() {
        wristbands = new ConcurrentHashMap<>();  //手环对应的集合   key为手环名称   value为整合后的数据（最终上传数据）
        fenceId_uwbData = new ConcurrentHashMap<>();//围栏id uwb设备对应关系  key为围栏id  value为uwb对象
        code_points = new ConcurrentHashMap<>();  //
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

    public ConcurrentHashMap<String, UwbQueue<Point>> getCode_points() {
        return code_points;
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
        if (!containFenceId(bleName)) {
            return null;
        }

        int fenceIdByBleName = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
        if (fenceIdByBleName == -1) {
            return null;
        }
        UWBCoordData uwbCoordData1 = FinalDataManager.getInstance().getFenceId_uwbData().get(fenceIdByBleName);
        if(uwbCoordData1==null){
            return null;
        }
        String code = uwbCoordData1.getCode();
        String bracelet_id = LinkDataManager.getInstance().getUwbCode_wristbandName().get(code);
        if(bracelet_id!=null){
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
         * 移除uwb
         *
         * @param fenceId
         */
        public void removeUwb(int fenceId){
            if(fenceId_uwbData.containsKey(fenceId)){
                fenceId_uwbData.remove(fenceId);
            }
        }



        /**
         * 器械是否已经绑定手环
         *
         * @param fenceId
         * @return
         */
        public boolean alreadyBind ( int fenceId){
            if(fenceId_uwbData.containsKey(fenceId)){
                return true;

            }
            return false;
        }

    }
