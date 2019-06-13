package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;

import java.math.BigDecimal;
import java.util.Arrays;

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
 * 椭圆机数据解析
 */
public class OvalProcessor implements IDataAnalysis {


    private static final float perimeter = 2.04f;   //单位 米
    float speed;

    public static OvalProcessor getInstance() {
        return OvalProcessorHolder.sOvalProcessor;
    }


    private static class OvalProcessorHolder {
        private static final OvalProcessor sOvalProcessor = new OvalProcessor();
    }

    @Override
    public BleDeviceInfo analysisBLEData(byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow = null;

        if (scanRecord == null) {
            return null;
        }
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        if (linkScanRecord == null) {
            return null;
        }
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        if (serviceData == null) {
            return null;
        }

        Log.i("vvvvvvv", Arrays.toString(serviceData));
        byte[] turns = new byte[2];
        turns[0] = serviceData[0];
        turns[1] = serviceData[1];


        byte[] ticks = new byte[2];
        ticks[0] = serviceData[3];
        ticks[1] = serviceData[2];

        if (CalculateUtil.byteArrayToInt(ticks) == 0) {
            speed = 0;
        } else if (turns[0] == -1 && turns[1] == -1) {
            speed = 0;
        } else {
            BigDecimal bigDecimal = CalculateUtil.floatDivision(perimeter, (float) CalculateUtil.byteArrayToInt(ticks));
            speed = calculateEllipticalSpeed(bigDecimal.floatValue() * 3600);
            Log.i("ticks", speed + "");
        }


//        Log.i("tttttttttttttt", Arrays.toString(scanRecord));
//        byte[] speed = new byte[1];
//        byte[] gradient = new byte[2];
//        speed[0] = scanRecord[11];
//        //  speed[1] = scanRecord[12];
//        gradient[0] = scanRecord[13];
//        gradient[1] = scanRecord[14];
//
//        int speedInt = Integer.parseInt(String.valueOf(CalculateUtil.byteArrayToInt(speed)));
//        int gradientInt = Integer.parseInt(String.valueOf(gradient[0]));


        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (deviceByBleName == null) {
            return null;
        }

        deviceByBleName.setAbility(speed);


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

        bleDeviceInfoNow.setSpeed(String.valueOf(speed));

        //椭圆机
//        if (speed == 0) {
//            bleDeviceInfoNow.setSpeed("0.0");
//        } else {
//            bleDeviceInfoNow.setSpeed(String.valueOf(calculateEllipticalSpeed(speed)));
//        }

        //  bleDeviceInfoNow.setGradient(String.valueOf(gradientInt));

        return bleDeviceInfoNow;

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


    private float calculateEllipticalSpeed(float measureSpeed) {
        BigDecimal bigDecimal = CalculateUtil.floatDivision(measureSpeed, 0.129164f);
        float v = (float) (1.837 * Math.pow(Math.E, (0.0259 * bigDecimal.floatValue())));
        if (v < 0) {
            return (float) 0;
        }
        return v;
    }


}
