package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * @author create by zhangyong
 * @time 2019/3/15
 */
public class LinkSpecificDevice {

    private int id;
    private String deviceName;
    private String anchName;
    private String type; //设备类型
    private float perimeter;
    private float slope;
 //   private float ability;  //设备的当前速度，判断是否在运转
    private UWBCoordData.FencePoint fencePoint;
    private UWBCoordData.FencePoint.Point  centerPoint;

    private long receiveDeviceBleTime;  //接收设备ble发出非零数据的时间

    private List<LinkBLE> linkBLES;

    public UWBCoordData.FencePoint.Point getCenterPoint() {
        return centerPoint;
    }

//    public float getAbility() {
//        return ability;
//    }

//    public void setAbility(float ability) {
//        if (ability == 0) {
//            receiveDeviceBleTime = 0;
//        } else if (this.ability == 0) {
//            receiveDeviceBleTime = System.currentTimeMillis();
//        }
//        this.ability = ability;
//
//
//    }


    public String getAnchName() {
        return anchName;
    }

    public void setAnchName(String anchName) {
        this.anchName = anchName;
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

    public float getPerimeter() {
        return perimeter;
    }

    public void setPerimeter(float perimeter) {
        this.perimeter = perimeter;
    }

    public float getSlope() {
        return slope;
    }

    public void setSlope(float slope) {
        this.slope = slope;
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

    public long getReceiveDeviceBleTime() {
        return receiveDeviceBleTime;
    }

    public void setCenterPoint(UWBCoordData.FencePoint.Point centerPoint) {
        this.centerPoint = centerPoint;
    }
}
