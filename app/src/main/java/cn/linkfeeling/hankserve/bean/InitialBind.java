package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * @author create by zhangyong
 * @time 2019/11/8
 */
public class InitialBind {


    /**
     * data : [{"bracelet":"I7D712","device":"飞鸟架01"}]
     * type : 160
     */

    private int type;
    private List<DataBean> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * bracelet : I7D712
         * device : 飞鸟架01
         */

        private String bracelet;
        private String device;

        public String getBracelet() {
            return bracelet;
        }

        public void setBracelet(String bracelet) {
            this.bracelet = bracelet;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }
    }
}
