package cn.linkfeeling.hankserve.subjects;


import android.os.ParcelUuid;
import android.util.Log;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 飞鸟数据解析
 */
public class FlyBirdProcessor implements IDataAnalysis {
    private int serialNum = -1;
    public static ConcurrentHashMap<String, FlyBirdProcessor> map;
    private static final float SELF_GRAVITY = 2.5f;

    static {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow = null;
        if (scanRecord == null) {
            return null;
        }
        Log.i("ppppppppp", Arrays.toString(scanRecord));
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        if (linkScanRecord == null) {
            return null;
        }
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        Log.i("999999999", Arrays.toString(serviceData));

        if (serviceData == null || serialNum == serviceData[11]) {
            return null;

        }
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (deviceByBleName == null) {
            return null;
        }
//        if(serviceData[0]!=0 && serviceData[0]!=-1){
//            deviceByBleName.setAbility(serviceData[0]);
//        }
        byte serviceTemp = serviceData[11];
        if (serviceTemp < serialNum && (serialNum - serviceTemp) <= 10) {
            return null;
        }
        deviceByBleName.setAbility(serviceData[0]);

        serialNum = serviceData[11];

        if (serviceData[0] == -1 && serviceData[1] == -1) {
            deviceByBleName.setAbility(0);

            byte act_time = serviceData[12];
            if (act_time == 0) {
                return null;
            }

            int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
            boolean containsKey = FinalDataManager.getInstance().getFenceId_uwbData().containsKey(fenceId);
            if (!containsKey) {
                return null;
            }
            UWBCoordData uwbCoordData = FinalDataManager.getInstance().getFenceId_uwbData().get(fenceId);

            String bracelet_id = uwbCoordData.getWristband().getBracelet_id();
            bleDeviceInfoNow = FinalDataManager.getInstance().getWristbands().get(bracelet_id);
            if (bleDeviceInfoNow == null) {
                return null;
            }
            byte gravity = serviceData[10];
            float actualGravity = SELF_GRAVITY * gravity;

            bleDeviceInfoNow.setGravity(String.valueOf(actualGravity));
            bleDeviceInfoNow.setTime(String.valueOf(act_time));
        }
        return bleDeviceInfoNow;

    }


}
