package cn.linkfeeling.hankserve.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author create by zhangyong
 * @time 2019/11/15
 */
public class TreadmillError extends BmobObject {

    private String gymName;
    private String deviceName;
    private byte[] rawData;
    private byte[] speedData;
    private String bleName;
    private int numbers;


    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public byte[] getRawData() {
        return rawData;
    }

    public void setRawData(byte[] rawData) {
        this.rawData = rawData;
    }

    public byte[] getSpeedData() {
        return speedData;
    }

    public void setSpeedData(byte[] speedData) {
        this.speedData = speedData;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }
}
