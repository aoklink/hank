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
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 飞鸟数据解析
 */
public class FlyBirdProcessor implements IDataAnalysis {
    private int serialNum = -1;
    public static ConcurrentHashMap<String, FlyBirdProcessor> map;

    static {
        map = new ConcurrentHashMap<>();
    }

    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow = null;
        if (scanRecord == null) {
            return null;
        }

//        byte[] result=new byte[16];
//
//        System.arraycopy(scanRecord,7, result, 0, 16);

        Log.i("ppppppppp", Arrays.toString(scanRecord));
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        if (linkScanRecord == null) {
            return null;
        }
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        Log.i("999999999", Arrays.toString(serviceData));

        if (serviceData == null || serialNum == serviceData[12]) {
            return null;

        }
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (deviceByBleName == null) {
            return null;
        }
//        if(serviceData[0]!=0 && serviceData[0]!=-1){
//            deviceByBleName.setAbility(serviceData[0]);
//        }


        deviceByBleName.setAbility(serviceData[0]);

        serialNum = serviceData[12];

        if (serviceData[0] == -1 && serviceData[1] == -1) {
            deviceByBleName.setAbility(0);

            byte act_time = serviceData[13];
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


            byte[] gravity = new byte[2];
            gravity[0] = serviceData[11];
            gravity[1] = serviceData[10];

            int act_gravity = CalculateUtil.byteArrayToInt(gravity);


            float v = CalculateUtil.txFloat(act_gravity, 100);
            bleDeviceInfoNow.setGravity(String.valueOf(v));
            bleDeviceInfoNow.setTime(String.valueOf(act_time));


        }
        //    byte[] bytes = serviceData.get(UUID.nameUUIDFromBytes("0000180a-0000-1000-8000-00805f9b34fb".getBytes()));

//        if (manufacturerSpecificData != null && manufacturerSpecificData.size() != 0) {
//            final byte[] bytes1 = manufacturerSpecificData.valueAt(0);
//            Log.i("fly bird", bleName + "-----" + Arrays.toString(bytes1));
//
//            LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
//            if (deviceByBleName == null) {
//                return null;
//            }
//            deviceByBleName.setAbility(bytes1[0]);
//
//            if (bytes1[0] == 0 && bytes1[12] == 0 && bytes1[14] == 0) {
//                return null;
//            }
//
//
//            if (serialNum == bytes1[10]) {2
//                return null;
//            }
//            serialNum = bytes1[10];
//
//
//            //在拉伸的过程中  原始数据不为0  但是质量和次数为0
//            if (bytes1[12] == 0 || bytes1[14] == 0) {
//                return null;
//            }
//
//
//            byte[] gravityBtye=new byte[1];
//            gravityBtye[0]=bytes1[12];
//            int gravity = byteArrayToInt(gravityBtye);
//
//
//            float v = CalculateUtil.txFloat(gravity, 10);
//            bleDeviceInfo.setGravity(String.valueOf(v));
//            Log.i("lllllllll",String.valueOf(v));
//            bleDeviceInfo.setTime(String.valueOf(bytes1[14]));
//        }
        return bleDeviceInfoNow;

    }


}
