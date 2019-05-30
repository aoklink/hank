package cn.linkfeeling.hankserve.interfaces;


import cn.linkfeeling.hankserve.bean.BleDeviceInfo;

/**
 * @author create by zhangyong
 * @time 2019/3/15
 */
public interface IDataAnalysis {

    BleDeviceInfo analysisBLEData(byte[] bytes, String bleName);
}
