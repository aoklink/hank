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
