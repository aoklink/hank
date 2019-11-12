package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/11/6
 */
public class WebPushBind {


    /**
     * bracelet : I7PLUSC9B5
     * type : 160
     * device : 跑步机01
     * status : true
     */

    private String bracelet;
    private int type;
    private String device;
    private boolean status;

    public String getBracelet() {
        return bracelet;
    }

    public void setBracelet(String bracelet) {
        this.bracelet = bracelet;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
