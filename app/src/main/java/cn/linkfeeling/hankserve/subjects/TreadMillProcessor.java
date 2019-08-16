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
import cn.linkfeeling.hankserve.queue.LimitQueue;
import cn.linkfeeling.hankserve.utils.CalculateUtil;
import cn.linkfeeling.hankserve.utils.LinkScanRecord;


/**
 * @author create by zhangyong
 * @time 2019/3/15
 * 跑步机数据解析
 */
public class TreadMillProcessor implements IDataAnalysis {

    private LimitQueue<Integer> limitQueue = new LimitQueue<Integer>(50);
    public static ConcurrentHashMap<String, TreadMillProcessor> map;
    private int flag = -1;


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
    public BleDeviceInfo analysisBLEData(String hostName, byte[] scanRecord, String bleName) {
        BleDeviceInfo bleDeviceInfoNow;
        LinkScanRecord linkScanRecord = LinkScanRecord.parseFromBytes(scanRecord);
        LinkSpecificDevice deviceByBleName = LinkDataManager.getInstance().getDeviceByBleName(bleName);
        if (scanRecord == null || linkScanRecord == null || deviceByBleName == null) {
            return null;
        }
        byte[] serviceData = linkScanRecord.getServiceData(ParcelUuid.fromString("0000180a-0000-1000-8000-00805f9b34fb"));
        if (serviceData == null) {
            return null;
        }



        byte[] pages = new byte[2];
        pages[0] = serviceData[2];
        pages[1] = serviceData[3];
        int nowPack = CalculateUtil.byteArrayToInt(pages);

        if (nowPack < flag && flag - nowPack < 10000) {
            return null;
        }

        if (limitQueue.contains(nowPack)) {
            return null;
        }
        Log.i("seqNum", nowPack + "");
        limitQueue.offer(nowPack);


        Log.i("6767676", Arrays.toString(serviceData));
        //检查是否有可绑定的手环  如果有则根据算法匹配
        LinkDataManager.getInstance().checkBind(deviceByBleName);


        float speed;
        if (serviceData[0] == -1 && serviceData[1] == -1) {
            flag = nowPack;
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

//        if (speed != 0) {
//            deviceByBleName.setAbility(speed);
//        }


        bleDeviceInfoNow = FinalDataManager.getInstance().containUwbAndWristband(bleName);
        if (bleDeviceInfoNow == null) {
            return null;
        }


        bleDeviceInfoNow.setSpeed(String.valueOf(speed));
        bleDeviceInfoNow.setSeq_num(String.valueOf(nowPack));

        if(speed==0){
            //解除绑定
            int fenceId = LinkDataManager.getInstance().getFenceIdByBleName(bleName);
            if (FinalDataManager.getInstance().getFenceId_uwbData().containsKey(fenceId)) {
                FinalDataManager.getInstance().removeUwb(fenceId);



                Log.i("666666666",FinalDataManager.getInstance().getFenceId_uwbData().size()+"");
                Log.i("666666666","移除了"+fenceId+"----"+LinkDataManager.getInstance().queryDeviceNameByFenceId(fenceId).getDeviceName());
            }
        }

        return bleDeviceInfoNow;

    }


}
