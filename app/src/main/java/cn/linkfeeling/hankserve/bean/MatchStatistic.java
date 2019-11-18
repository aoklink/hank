package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/11/13
 */
public class MatchStatistic {
    private String deviceName;
    private String bleName;
    private String gymName;
    private String watchName;
    private String sensorNum;
    private String algorithmWatchNum;
    private String algorithmDeviceNum;
    private String type;

    public MatchStatistic() {
        this.type = "MatchStatistic";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getWatchName() {
        return watchName;
    }

    public void setWatchName(String watchName) {
        this.watchName = watchName;
    }

    public String getSensorNum() {
        return sensorNum;
    }

    public void setSensorNum(String sensorNum) {
        this.sensorNum = sensorNum;
    }

    public String getAlgorithmWatchNum() {
        return algorithmWatchNum;
    }

    public void setAlgorithmWatchNum(String algorithmWatchNum) {
        this.algorithmWatchNum = algorithmWatchNum;
    }

    public String getAlgorithmDeviceNum() {
        return algorithmDeviceNum;
    }

    public void setAlgorithmDeviceNum(String algorithmDeviceNum) {
        this.algorithmDeviceNum = algorithmDeviceNum;
    }
}
