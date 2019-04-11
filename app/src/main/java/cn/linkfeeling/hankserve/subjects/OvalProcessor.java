package cn.linkfeeling.hankserve.subjects;

import android.util.Log;

import java.util.Arrays;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.LinkDataManager;

/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 椭圆机数据解析
 */
public class OvalProcessor implements IDataAnalysis {

    public static OvalProcessor getInstance() {
        return OvalProcessorHolder.sOvalProcessor;
    }

    private static class OvalProcessorHolder {
        private static final OvalProcessor sOvalProcessor = new OvalProcessor();
    }

    @Override
    public BleDeviceInfo analysisBLEData(BleDeviceInfo bleDeviceInfo, byte[] scanRecord, String bleName) {
        if (scanRecord != null) {

            Log.i("tttttttttttttt",Arrays.toString(scanRecord));
            byte[] speed = new byte[2];
            byte[] gradient = new byte[2];
            speed[0] = scanRecord[11];
            speed[1] = scanRecord[12];
            gradient[0] = scanRecord[13];
            gradient[1] = scanRecord[14];

            int speedInt = Integer.parseInt(String.valueOf(speed[0]));
            int gradientInt = Integer.parseInt(String.valueOf(gradient[0]));


            LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
            if (deviceByBleName == null) {
                return null;
            }

            //椭圆机
            if (speedInt == 0) {
                bleDeviceInfo.setSpeed("0.0");
            } else {
                bleDeviceInfo.setSpeed(String.valueOf(calculateEllipticalSpeed(speedInt)));
            }

            bleDeviceInfo.setGradient(String.valueOf(gradientInt));
            deviceByBleName.setAbility(speedInt);
        }
        return bleDeviceInfo;

    }

    /**
     * 计算椭圆机的速度（椭圆机）
     *
     * @author zhangyong
     * @time 2019/3/18 16:44
     */
    private float calculateEllipticalSpeed(int rotate) {
        float v = (float) (1.837 * Math.pow(Math.E, (0.0259 * rotate)));
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }

}
