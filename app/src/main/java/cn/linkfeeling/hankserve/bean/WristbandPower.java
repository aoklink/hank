package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * @author create by zhangyong
 * @time 2019/9/24
 */
public class WristbandPower {


    /**
     * gym_name : link_test
     * data : [{"bracelet_id":"I7PLUSE9FC","battery":"21"},{"bracelet_id":"I7PLUSE9FC","battery":"21"},{"bracelet_id":"I7PLUSE9FC","battery":"21"}]
     */

    private String gym_name;
    private List<DataBean> data;

    public String getGym_name() {
        return gym_name;
    }

    public void setGym_name(String gym_name) {
        this.gym_name = gym_name;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * bracelet_id : I7PLUSE9FC
         * battery : 21
         */

        private String bracelet_id;
        private String battery;

        public String getBracelet_id() {
            return bracelet_id;
        }

        public void setBracelet_id(String bracelet_id) {
            this.bracelet_id = bracelet_id;
        }

        public String getBattery() {
            return battery;
        }

        public void setBattery(String battery) {
            this.battery = battery;
        }
    }
}
