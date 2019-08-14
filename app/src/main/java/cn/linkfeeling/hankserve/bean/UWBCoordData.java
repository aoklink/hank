package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * UWB获取的标签位置信息
 *
 * @author create by zhangyong
 * @time 2019/3/14
 */
public class UWBCoordData {
    /**
     * id : 7
     * code : 00000b33
     * mac : 00000b33
     * anchor : 90000604
     * anchorCode : 90000604
     * map : linkfeeling01_1
     * mapId : 1
     * time : 1552531766097
     * msgType : coord
     * anchorList : ["90000600","90000601","90000604"]
     * createTime : 2019-03-14 10:49:26.97
     * x : 353.4778088036451
     * y : 1454.7944197588838
     * z : 0
     * rate : 0.01
     */

    private String id;
    private String code;
    private String mac;
    private String anchor;
    private String anchorCode;
    private String map;
    private String mapId;
    private long time;
    private String msgType;
    private String createTime;
    private double x;
    private double y;
    private int z;
    private String rate;
    private List<String> anchorList;
    private LinkSpecificDevice device;
    private Wristband wristband;

    private int semaphore;//信号量

    public int getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(int semaphore) {
        this.semaphore = semaphore;
    }

    public Wristband getWristband() {
        return wristband;
    }

    public void setWristband(Wristband wristband) {
        this.wristband = wristband;
    }

    public LinkSpecificDevice getDevice() {
        return device;
    }

    public void setDevice(LinkSpecificDevice device) {
        this.device = device;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public String getAnchorCode() {
        return anchorCode;
    }

    public void setAnchorCode(String anchorCode) {
        this.anchorCode = anchorCode;
    }

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public List<String> getAnchorList() {
        return anchorList;
    }

    public void setAnchorList(List<String> anchorList) {
        this.anchorList = anchorList;
    }


    public static class FencePoint {
        private int fenceId;
        private Point left_top;
        private Point right_top;
        private Point left_bottom;
        private Point right_bottom;


        public int getFenceId() {
            return fenceId;
        }

        public void setFenceId(int fenceId) {
            this.fenceId = fenceId;
        }


        public Point getLeft_top() {
            return left_top;
        }

        public void setLeft_top(Point left_top) {
            this.left_top = left_top;
        }

        public Point getRight_top() {
            return right_top;
        }

        public void setRight_top(Point right_top) {
            this.right_top = right_top;
        }

        public Point getLeft_bottom() {
            return left_bottom;
        }

        public void setLeft_bottom(Point left_bottom) {
            this.left_bottom = left_bottom;
        }

        public Point getRight_bottom() {
            return right_bottom;
        }

        public void setRight_bottom(Point right_bottom) {
            this.right_bottom = right_bottom;
        }

        public static class Point {

            public double x;
            public double y;

            public Point(double x, double y) {
                this.x = x;
                this.y = y;
            }

            public double getX() {
                return x;
            }

            public void setX(double x) {
                this.x = x;
            }

            public double getY() {
                return y;
            }

            public void setY(double y) {
                this.y = y;
            }
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;//地址相等
        }

        if (obj == null) {
            return false;//非空性：对于任意非空引用x，x.equals(null)应该返回false。
        }

        if (obj instanceof UWBCoordData) {
            UWBCoordData other = (UWBCoordData) obj;
            //需要比较的字段相等，则这两个对象相等
            if (this.getCode().equals(other.getCode())) {
                return true;
            }
        }

        return false;
    }
}
