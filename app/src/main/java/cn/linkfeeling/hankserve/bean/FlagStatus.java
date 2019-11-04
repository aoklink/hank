package cn.linkfeeling.hankserve.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author create by zhangyong
 * @time 2019/11/4
 */
public class FlagStatus extends BmobObject {

    private int seq;
    private int flag;
    private String deviceName;
    private String bleName;
    private String gymName;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }
}
