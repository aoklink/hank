package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/9/17
 */
public class MatchResult {

    private String deviceName;
    private String wristband;
    private int matchResult;
    private String deviceSeq;
    private String watchSeq;

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

    public int getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(int matchResult) {
        this.matchResult = matchResult;
    }

    public String getDeviceSeq() {
        return deviceSeq;
    }

    public void setDeviceSeq(String deviceSeq) {
        this.deviceSeq = deviceSeq;
    }

    public String getWatchSeq() {
        return watchSeq;
    }

    public void setWatchSeq(String watchSeq) {
        this.watchSeq = watchSeq;
    }
}
