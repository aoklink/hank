package cn.linkfeeling.hankserve.interfaces;


import cn.linkfeeling.hankserve.bean.BleDeviceInfo;

/**
 * @author create by zhangyong
 * @time 2019/5/10
 */
public abstract class IAnchDataAnalysis implements IDataAnalysis {

    @Override
    public BleDeviceInfo analysisBLEData(String hostName,byte[] bytes, String bleName) {
        return null;
    }

   public abstract void analysisAnchData(byte[] bytes, String bleName);

}
