package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * @author create by zhangyong
 * @time 2019/10/9
 */
public class DevicePower {


    /**
     * gym_name : link_test
     * data : [{"device_id":"1","device":"飞鸟01","serial_no":"3212321","battery":"21"},{"device_id":"2","device":"飞鸟02","serial_no":"11321321","battery":"41"},{"device_id":"3","device":"飞鸟03","serial_no":"22321321","battery":"51"}]
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
         * device_id : 1
         * device : 飞鸟01
         * serial_no : 3212321
         * battery : 21
         */

        private String device_id;
        private String device;
        private String serial_no;
        private String battery;

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getDevice() {
            return device;
        }

        public void setDevice(String device) {
            this.device = device;
        }

        public String getSerial_no() {
            return serial_no;
        }

        public void setSerial_no(String serial_no) {
            this.serial_no = serial_no;
        }

        public String getBattery() {
            return battery;
        }

        public void setBattery(String battery) {
            this.battery = battery;
        }
    }
}
