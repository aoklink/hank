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
 * 跑步机数据解析
 */
public class TreadMillProcessor implements IDataAnalysis {

    private int currentPags;

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
    public BleDeviceInfo analysisBLEData(String hostName,byte[] scanRecord, String bleName) {
        Log.i("pppppppppppppp", Arrays.toString(scanRecord));
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

        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (deviceByBleName == null) {
            return null;
        }

        Log.i("6767676", Arrays.toString(serviceData));

        byte[] pages = new byte[2];
        pages[0] = serviceData[2];
        pages[1] = serviceData[3];
        int nowPack = CalculateUtil.byteArrayToInt(pages);
        //此处取5是因为包数一秒增加1  所以控制为延迟5秒的数据
        if (nowPack < currentPags && currentPags - nowPack < 5) {
            return null;
        }

        currentPags = nowPack;

        float speed;
        if (serviceData[0] == -1 && serviceData[1] == -1) {
            speed = 0;
        } else {
            byte[] serviceDatum = new byte[2];
            serviceDatum[0] = serviceData[11];
            serviceDatum[1] = serviceData[12];
            int numbers = CalculateUtil.byteArrayToInt(serviceDatum);
            Log.i("67676", numbers + "");
            float v = CalculateUtil.txFloat(numbers, 100);

            speed = v * 0.25641026f;
            //0.256410
        }

        Log.i("6767676", speed + "");

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

        return bleDeviceInfoNow;

    }


}
