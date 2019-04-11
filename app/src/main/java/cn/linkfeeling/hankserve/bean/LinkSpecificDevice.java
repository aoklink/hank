package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * @author create by zhangyong
 * @time 2019/3/15
 */
public class LinkSpecificDevice {

    private int id;
    private String deviceName;
    private String type; //设备类型
    private float ability;  //设备的当前速度，判断是否在运转
    private UWBCoordData.FencePoint fencePoint;

    private List<LinkBLE> linkBLES;

    public float getAbility() {
        return ability;
    }

    public void setAbility(float ability) {
        this.ability = ability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public UWBCoordData.FencePoint getFencePoint() {
        return fencePoint;
    }

    public void setFencePoint(UWBCoordData.FencePoint fencePoint) {
        this.fencePoint = fencePoint;
    }

    public List<LinkBLE> getLinkBLES() {
        return linkBLES;
    }

    public void setLinkBLES(List<LinkBLE> linkBLES) {
        this.linkBLES = linkBLES;
    }
}
