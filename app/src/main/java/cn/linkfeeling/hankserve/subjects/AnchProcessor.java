package cn.linkfeeling.hankserve.subjects;

import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.hankserve.bean.AccelData;
import cn.linkfeeling.hankserve.bean.BleDeviceInfo;
import cn.linkfeeling.hankserve.bean.LinkSpecificDevice;
import cn.linkfeeling.hankserve.bean.Power;
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
    private final static String I7 = "I7";
    private final static String I7PLUS = "I7PLUS";
    public static ConcurrentHashMap<String, AnchProcessor> map;
    public static ConcurrentHashMap<String, String> mac_label;

    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);

    static {
        map = new ConcurrentHashMap<>();
        mac_label = new ConcurrentHashMap<>();
        mac_label.put("DD1C",I7PLUS);
        mac_label.put("C9AF",I7PLUS);
        mac_label.put("E5BE",I7PLUS);
        mac_label.put("C12D",I7PLUS);
        mac_label.put("DA51",I7PLUS);
        mac_label.put("C1B3",I7PLUS);
        mac_label.put("E9FC",I7PLUS);
        mac_label.put("D30D",I7PLUS);
        mac_label.put("EA29",I7PLUS);
        mac_label.put("CFA3",I7PLUS);
        mac_label.put("CF78",I7PLUS);
        mac_label.put("CE9D",I7PLUS);
        mac_label.put("EBF1",I7PLUS);
        mac_label.put("E280",I7PLUS);
        mac_label.put("CD28",I7PLUS);
        mac_label.put("F8F7",I7PLUS);
        mac_label.put("D08D",I7PLUS);
        mac_label.put("F4A6",I7PLUS);

    }


    public static AnchProcessor getInstance() {
        return AnchProcessorHolder.anchProcessor;
    }

    @Override
    public void analysisAnchData(byte[] bytes, String bleName) {
        //[2, 1, 6, 19, 22, 10, 24, 3, -3, -95, -44, -41, 18, -44, -27, -66, -50, 2, 54, 0, 0, 0, 0, 7, 9, 65, 78, 67, 72, 48, 49, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
        Log.i("anchBLE", Arrays.toString(bytes));
        Log.i("anchBLE", bleName);
        //[2, -27, -66, -59, -35, 28, -62, 0, 0, 0, 0, 0, 0, 1, 115, 3]
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(bytes);
        LinkSpecificDevice deviceByanchName = LinkDataManager.getInstance().getDeviceByanchName(bleName);

        if (linkScanRecord == null || deviceByanchName == null) {
            return;
        }

        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        if (serviceData == null) {
            return;
        }
        Log.i("yyyyyyyyyyyyy", Arrays.toString(serviceData));

        byte serviceDatum = serviceData[0];

        byte[] seqNum = {serviceData[13], serviceData[14]};

/*        if (limitQueue.contains(CalculateUtil.byteArrayToInt(seqNum))) {
            return;
        }
        limitQueue.offer(CalculateUtil.byteArrayToInt(seqNum));*/

        boolean b = dealPowerData(serviceData, deviceByanchName, bleName);
        if (b) {
            return;
        }

        if (serviceDatum > 0) {
            byte[] mac = new byte[2];
            mac[0] = serviceData[1];
            mac[1] = serviceData[2];
            String macName = HexUtil.encodeHexStr(mac);
            String label = mac_label.get(macName);
            if (label != null) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(label);
                stringBuilder.append(macName);
                Log.i("21212121", stringBuilder.toString() + "---" + serviceData[3]);
                FinalDataManager.getInstance().getRssi_wristbands().put(bleName, stringBuilder.toString());
            }
        }

    }


    private boolean dealPowerData(byte[] serviceData, LinkSpecificDevice deviceByBleName, String bleName) {
        //  [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1]
        if (serviceData[0] == 0 &&
                serviceData[1] == 0 &&
                serviceData[2] == 0 &&
                serviceData[3] == 0 &&
                serviceData[4] == 0 &&
                serviceData[5] == 0 &&
                serviceData[6] == 0 &&
                serviceData[7] == 0 &&
                serviceData[8] == 0 &&
                serviceData[9] == 0 &&
                serviceData[10] == 0 &&
                serviceData[11] == 0 &&
                serviceData[12] == 0) {


       /*     Power power1 = new Power();
            power1.setDeviceName(deviceByBleName.getDeviceName());
            power1.setBleNme(bleName);
            power1.setPowerLevel(CalculateUtil.byteToInt(serviceData[15]));
            power1.setGymName(BuildConfig.PROJECT_NAME);


            power1.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {

                    Log.i("99999-----", s == null ? "null" : s);
                    Log.i("99999eeeee", e == null ? "null" : e.getMessage());
                }
            });*/
            return true;
        }
        return false;
    }

    private static class AnchProcessorHolder {
        private static final AnchProcessor anchProcessor = new AnchProcessor();
    }

}
