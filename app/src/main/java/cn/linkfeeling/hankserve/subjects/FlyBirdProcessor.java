package cn.linkfeeling.hankserve.subjects;


import android.os.ParcelUuid;
import android.util.Log;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
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
    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);

    static {
        map = new ConcurrentHashMap<>();
    }


    private Vector<Integer> list = new Vector<>();

    @Override
    public BleDeviceInfo analysisBLEData(String hostName, byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow;
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (scanRecord == null || linkScanRecord == null || deviceByBleName == null) {
            return null;
        }
        Log.i("ppppppppp" + bleName, Arrays.toString(scanRecord));
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        Log.i("999999999" + bleName + "--" + hostName, Arrays.toString(serviceData));

        if (serviceData == null) {
            return null;
        }
//        if(serviceData[0]!=0 && serviceData[0]!=-1){
//            deviceByBleName.setAbility(serviceData[0]);
//        }
        byte serviceTemp = serviceData[11];

        if (limitQueue.contains(CalculateUtil.byteToInt(serviceTemp))) {
            return null;
        }
        Log.i("seqNum", CalculateUtil.byteToInt(serviceTemp) + "");
        limitQueue.offer(CalculateUtil.byteToInt(serviceTemp));
        deviceByBleName.setAbility(serviceData[0]);

        int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
        boolean containsKey = FinalDataManager.getInstance().getFenceId_uwbData().containsKey(fenceId);
        if (!containsKey) {
            deviceByBleName.setAbility(0);
            return null;
        }
        UWBCoordData uwbCoordData = FinalDataManager.getInstance().getFenceId_uwbData().get(fenceId);
        String bracelet_id = uwbCoordData.getWristband().getBracelet_id();
        bleDeviceInfoNow = FinalDataManager.getInstance().getWristbands().get(bracelet_id);
        if (bleDeviceInfoNow == null) {
            deviceByBleName.setAbility(0);
            return null;
        }


        if (serviceData[0] != -1 && serviceData[0] != 0 && serviceData[1] != -1 && serviceData[1] != 0) {
            for (int j = 0; j < 10; j++) {
                int cuv1 = CalculateUtil.byteToInt(serviceData[j]);
                bleDeviceInfoNow.getCurve().add(cuv1);
                //  list.add(cuv1);
            }
        }


        if (serviceData[0] == -1 && serviceData[1] == -1) {

//            Log.i("iiiiiiiiiiiii", JSON.toJSONString(list));
            Log.i("iiiiiiiiiiiii", JSON.toJSONString(limitQueue));
//            list.clear();
            if (serviceData[12] == 0) {
                deviceByBleName.setAbility(0);
                return null;
            }
            byte act_time = serviceData[12];
            byte gravity = serviceData[10];
            float actualGravity = SELF_GRAVITY * gravity;


            byte[] u_time = new byte[2];
            u_time[0] = serviceData[13];
            u_time[1] = serviceData[14];

            bleDeviceInfoNow.setGravity(String.valueOf(actualGravity));
            bleDeviceInfoNow.setTime(String.valueOf(act_time));
            bleDeviceInfoNow.setU_time(String.valueOf(CalculateUtil.byteArrayToInt(u_time)));
            deviceByBleName.setAbility(0);
        }
        return bleDeviceInfoNow;

    }


}
