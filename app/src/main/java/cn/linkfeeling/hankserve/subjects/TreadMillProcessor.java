package cn.linkfeeling.hankserve.subjects;

import android.util.Log;

import java.util.Arrays;

import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.interfaces.IDataAnalysis;
import cn.linkfeeling.hankserve.manager.LinkDataManager;

import static cn.linkfeeling.hankserve.utils.CalculateUtil.txFloat;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 跑步机数据解析
 */
public class TreadMillProcessor implements IDataAnalysis {

    private int speedNum = -1;
    private int i, j;


    public static TreadMillProcessor getInstance() {
        return TreadMillProcessorHolder.sTreadMillProcessor;
    }

    private static class TreadMillProcessorHolder {
        private static final TreadMillProcessor sTreadMillProcessor = new TreadMillProcessor();
    }

    @Override
    public BleDeviceInfo analysisBLEData(BleDeviceInfo bleDeviceInfo, byte[] scanRecord, String bleName) {
        Log.i("pppppppppppppp", Arrays.toString(scanRecord));
        if (scanRecord != null) {
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

            Log.i("ooooooooooo", speedInt + "");

            float floatSpeed = txFloat(speedInt, 10);

            Log.i("ooooooooooo11", floatSpeed + "");
            float floatGradient = txFloat(gradientInt, 10);


            deviceByBleName.setAbility(floatSpeed);


            bleDeviceInfo.setSpeed(String.valueOf(floatSpeed));
            bleDeviceInfo.setGradient(String.valueOf(floatGradient));

            if (speedInt != 52) {
                speedNum = speedInt;
            }

            if (speedNum == 0 && speedInt == 52) {
                i++;
            }

            if (speedNum != 0 && speedInt == 52) {
                j++;
            }

            Log.i("静止的出现的数量", i + "");
            Log.i("运动的出现的数量", j + "");


        }
        return bleDeviceInfo;

    }
}
