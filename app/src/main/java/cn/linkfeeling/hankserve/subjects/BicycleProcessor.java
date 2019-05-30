package cn.linkfeeling.hankserve.subjects;

import android.util.Log;

import java.util.Arrays;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.UWBCoordData;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.utils.CalculateUtil;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 单车数据解析
 */
public class BicycleProcessor implements IDataAnalysis {

    public static BicycleProcessor getInstance() {
        return BicycleProcessorHolder.sBicycleProcessor;
    }

    private static class BicycleProcessorHolder {
        private static final BicycleProcessor sBicycleProcessor = new BicycleProcessor();
    }


    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow = null;
        if (scanRecord != null) {
            Log.i("ddddddddddddddddd", Arrays.toString(scanRecord));


            byte[] speed = new byte[1];
            byte[] gradient = new byte[2];
            speed[0] = scanRecord[11];
            //  speed[1] = scanRecord[12];
            gradient[0] = scanRecord[13];
            gradient[1] = scanRecord[14];

            int speedInt = Integer.parseInt(String.valueOf(CalculateUtil.byteArrayToInt(speed)));
            int gradientInt = Integer.parseInt(String.valueOf(gradient[0]));

            LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
            if (deviceByBleName == null) {
                return null;
            }
            deviceByBleName.setAbility(speedInt);


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


            //单车
            if (speedInt == 0) {
                bleDeviceInfoNow.setSpeed("0.0");

            } else {
                bleDeviceInfoNow.setSpeed(String.valueOf(calculateBicycleSpeed(speedInt)));
            }

            bleDeviceInfoNow.setGradient(String.valueOf(gradientInt));

        }

        return bleDeviceInfoNow;

    }

    /**
     * 根据转速换算速度(单车)
     *
     * @author zhangyong
     * @time 2019/3/18 14:01
     */
    private double calculateBicycleSpeed(int rotate) {
        float v = (float) ((rotate * 0.3378) - 7.3649);
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }

}
