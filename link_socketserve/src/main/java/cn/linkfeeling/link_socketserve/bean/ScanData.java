package cn.linkfeeling.link_socketserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/4/9
 */
public class ScanData {

    private String name;
    private  int rssi;
    private byte[] scanRecord;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }
}
