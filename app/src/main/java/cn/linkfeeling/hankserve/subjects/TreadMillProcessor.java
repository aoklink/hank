package cn.linkfeeling.hankserve.subjects;

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

import static cn.linkfeeling.hankserve.utils.CalculateUtil.txFloat;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 跑步机数据解析
 */
public class TreadMillProcessor implements IDataAnalysis {

    public static ConcurrentHashMap<String, TreadMillProcessor> map;

    static {
        map = new ConcurrentHashMap<>();
    }

    public static TreadMillProcessor getInstance() {
        return TreadMillProcessorHolder.sTreadMillProcessor;
    }

    private static class TreadMillProcessorHolder {
        private static final TreadMillProcessor sTreadMillProcessor = new TreadMillProcessor();
    }

    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        Log.i("pppppppppppppp", Arrays.toString(scanRecord));
        BleDeviceInfo bleDeviceInfoNow = null;

        if (scanRecord != null) {
            byte[] speed = new byte[1];
            byte[] gradient = new byte[2];
            speed[0] = scanRecord[11];
            // speed[1] = scanRecord[12];
            gradient[0] = scanRecord[13];
            gradient[1] = scanRecord[14];

            int speedInt = Integer.parseInt(String.valueOf(CalculateUtil.byteArrayToInt(speed)));
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


            bleDeviceInfoNow.setSpeed(String.valueOf(floatSpeed));
            bleDeviceInfoNow.setGradient(String.valueOf(floatGradient));

        }
        return bleDeviceInfoNow;

    }


}
