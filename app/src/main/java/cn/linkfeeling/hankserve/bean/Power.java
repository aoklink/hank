package cn.linkfeeling.hankserve.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author create by zhangyong
 * @time 2019/8/5
 */
public class Power extends BmobObject {
    private String deviceName;
    private String bleNme;
    private int  powerLevel;
    private String gymName;

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

    public String getBleNme() {
        return bleNme;
    }

    public void setBleNme(String bleNme) {
        this.bleNme = bleNme;
    }

    public int getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(int powerLevel) {
        this.powerLevel = powerLevel;
    }
}
