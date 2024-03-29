package cn.linkfeeling.hankserve.subjects;

import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import cn.linkfeeling.hankserve.bean.AccelData;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.interfaces.IWristbandDataAnalysis;
import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.queue.MatchQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;
import cn.linkfeeling.hankserve.utils.WatchScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 手环数据解析
 */
public class WristbandProcessor extends IWristbandDataAnalysis {

    public static ConcurrentHashMap<String, WristbandProcessor> map;

    private LimitQueue<Integer> limitQueue = new LimitQueue<>(50);

    private MatchQueue<AccelData> watchQueue = new MatchQueue<>(360);
    private LimitQueue<Integer> watchSeq = new LimitQueue<>(72);

    static {
        map = new ConcurrentHashMap<>();
    }


    public static WristbandProcessor getInstance() {
        return WristbandProcessor.WristbandProcessorHolder.sWristbandProcessor;
    }

    private static class WristbandProcessorHolder {
        private static final WristbandProcessor sWristbandProcessor = new WristbandProcessor();
    }


    @Override
    public synchronized BleDeviceInfo analysisWristbandData(BleDeviceInfo bleDeviceInfo, byte[] bytes, String bleName) {
        if (bytes == null) {
            return null;
        }

        Log.i("hhhhhhhhhhhhhhhh" + bleName, Arrays.toString(bytes));


        if (bleName.contains("I7PLUS")) {
            LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(bytes);
            if (linkScanRecord == null) {
                return null;
            }
            SparseArray<byte[]> manufacturerSpecificData = linkScanRecord.getManufacturerSpecificData();
            if (manufacturerSpecificData != null && manufacturerSpecificData.size() != 0) {
                byte[] bytes1 = manufacturerSpecificData.valueAt(0);
                if (bytes1 == null || bytes1.length == 0) {
                    return null;
                }
                byte[] heart = new byte[1];
                heart[0] = bytes1[6];
                int heartInt = CalculateUtil.byteArrayToInt(heart);
                String heatRate = String.valueOf(heartInt);


                byte power = bytes1[9];

                Log.i(bleName + "power", CalculateUtil.byteToInt(power) + "");

                Log.i("cccccccccccccccc" + bleName, heatRate);
                bleDeviceInfo.setBracelet_id(bleName);
                bleDeviceInfo.setHeart_rate(heatRate);
            }
        } else if (bleName.contains("I7")) {

            WatchScanRecord watchScanRecord = WatchScanRecord.parseFromBytes(bytes);
            if (watchScanRecord == null) {
                return null;
            }
            SparseArray<byte[]> manufacturerSpecificData = watchScanRecord.getManufacturerSpecificData();
            if (manufacturerSpecificData != null && manufacturerSpecificData.size() != 0) {
                byte[] bytes1 = manufacturerSpecificData.valueAt(0);
                Log.i("shshshsh" + bleName, Arrays.toString(bytes1));
                if (bytes1 == null || bytes1.length == 0) {
                    return null;
                }

                byte[] seqNum = new byte[2];
                seqNum[0] = bytes1[4];
                seqNum[1] = bytes1[3];
                int seq = CalculateUtil.byteArrayToInt(seqNum);
                if (limitQueue.contains(seq)) {
                    return null;
                }
                limitQueue.offer(seq);
                watchSeq.offer(seq);

                Log.i("power"+bleName,CalculateUtil.byteToInt(bytes1[21])+"");
                LinkDataManager.getInstance().getWristPowerMap().put(bleName,CalculateUtil.byteToInt(bytes1[21]));

                Log.i("bvbvbv" + bleName, seq + "");

                for (int j = 5; j < 20; j = j + 3) {
                    AccelData accelData = new AccelData();
                    accelData.setX(bytes1[j]);
                    accelData.setY(bytes1[j + 1]);
                    accelData.setZ(bytes1[j + 2]);
                    watchQueue.offer(accelData);
                }


                byte[] heart = new byte[1];
                heart[0] = bytes1[2];
                int heartInt = CalculateUtil.byteArrayToInt(heart);
                String heatRate = String.valueOf(heartInt);

                Log.i("cccccccccccccccc" + bleName, heatRate);
                bleDeviceInfo.setBracelet_id(bleName);
                bleDeviceInfo.setHeart_rate(heatRate);
            }
        }

        return bleDeviceInfo;
    }

    public MatchQueue<AccelData> getWatchQueue() {
        return watchQueue;
    }

    public LimitQueue<Integer> getWatchSeq() {
        return watchSeq;
    }
}
