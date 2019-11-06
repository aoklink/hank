package cn.linkfeeling.hankserve.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author create by zhangyong
 * @time 2019/11/6
 */
public class FinalBind extends BmobObject {

    private String watchName;
    private String uwbCode;
    private String deviceName;
    private String gymName;

    public String getWatchName() {
        return watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getUwbCode() {
        return uwbCode;
    }

    public void setUwbCode(String uwbCode) {
        this.uwbCode = uwbCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }


    public String getGymName() {
        return gymName;
    }

    public void setGymName(String gymName) {
        this.gymName = gymName;
    }
}
