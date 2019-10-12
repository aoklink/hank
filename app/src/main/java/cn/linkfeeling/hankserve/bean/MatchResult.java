package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/9/17
 */
public class MatchResult {

    private String deviceName;
    private String wristband;
    private String  match_time;
    private String match_two;
    private String match_three;
    private String watchNum;
    private String deviceNum;
    private boolean watchStatus;
    private boolean deviceStatus;

    public String getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(String watchNum) {
        this.watchNum = watchNum;
    }

    public String getDeviceNum() {
        return deviceNum;
    }

    public void setDeviceNum(String deviceNum) {
        this.deviceNum = deviceNum;
    }

    public void setWatchStatus(boolean watchStatus) {
        this.watchStatus = watchStatus;
    }

    public void setDeviceStatus(boolean deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public boolean isWatchStatus() {
        return watchStatus;
    }

    public boolean isDeviceStatus() {
        return deviceStatus;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getWristband() {
        return wristband;
    }

    public void setWristband(String wristband) {
        this.wristband = wristband;
    }

    public String getMatch_time() {
        return match_time;
    }

    public void setMatch_time(String match_time) {
        this.match_time = match_time;
    }

    public String getMatch_two() {
        return match_two;
    }

    public void setMatch_two(String match_two) {
        this.match_two = match_two;
    }

    public String getMatch_three() {
        return match_three;
    }

    public void setMatch_three(String match_three) {
        this.match_three = match_three;
    }
}
