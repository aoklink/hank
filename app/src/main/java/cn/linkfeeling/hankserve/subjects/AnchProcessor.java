package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.AccelData;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.interfaces.IAnchDataAnalysis;
import cn.linkfeeling.hankserve.interfaces.IWristbandDataAnalysis;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.queue.MatchQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.HexUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;
import cn.linkfeeling.hankserve.utils.WatchScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 手环数据解析
 */
public class AnchProcessor extends IAnchDataAnalysis {

    public static ConcurrentHashMap<String, AnchProcessor> map;

    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);

    static {
        map = new ConcurrentHashMap<>();
    }


    public static AnchProcessor getInstance() {
        return AnchProcessorHolder.anchProcessor;
    }

    @Override
    public void analysisAnchData(byte[] bytes, String bleName) {
        //[2, 1, 6, 19, 22, 10, 24, 3, -3, -95, -44, -41, 18, -44, -27, -66, -50, 2, 54, 0, 0, 0, 0, 7, 9, 65, 78, 67, 72, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]

        Log.i("anchBLE", Arrays.toString(bytes));
        Log.i("anchBLE", bleName);

        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(bytes);
        if (linkScanRecord == null) {
            return;
        }

        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        if (serviceData == null) {
            return;
        }
        Log.i("yyyyyyyyyyyyy", Arrays.toString(serviceData));

        byte serviceDatum = serviceData[0];

        byte[] seqNum = {serviceData[(serviceDatum * 3) + 1], serviceData[(serviceDatum * 3) + 2]};

        if (limitQueue.contains(CalculateUtil.byteArrayToInt(seqNum))) {
            return;
        }
        limitQueue.offer(CalculateUtil.byteArrayToInt(seqNum));

        if (serviceDatum > 0) {
            byte[] mac = new byte[2];
            mac[0] = serviceData[1];
            mac[1] = serviceData[2];
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("I7PLUS");
            stringBuilder.append(HexUtil.encodeHexStr(mac));
            Log.i("21212121",stringBuilder.toString());
            FinalDataManager.getInstance().getRssi_wristbands().put(bleName, stringBuilder.toString());
        }

    }

    private static class AnchProcessorHolder {
        private static final AnchProcessor anchProcessor = new AnchProcessor();
    }

}
