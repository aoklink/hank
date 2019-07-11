package cn.linkfeeling.hankserve.bean;

/**
 * @author create by zhangyong
 * @time 2019/3/15
 */
public class LinkBLE {

    private int bleId;
    private String bleName;
    private String type;   //蓝牙类型   对应不同的解析方式
    private float[] weight;

    public int getBleId() {
        return bleId;
    }

    public void setBleId(int bleId) {
        this.bleId = bleId;
    }

    public String getBleName() {
        return bleName;
    }

    public void setBleName(String bleName) {
        this.bleName = bleName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float[] getWeight() {
        return weight;
    }

    public void setWeight(float[] weight) {
        this.weight = weight;
    }
}
