package cn.linkfeeling.hankserve.bean;

import java.util.List;
import java.util.Vector;

/**
 * @author create by zhangyong
 * @time 2019/3/13
 */
public class BleDeviceInfo implements Cloneable {
    /**
     * bracelet_id : 00000943
     * heart_rate : 98
     * device_name : 跑步机1
     * gym_name : link_office
     * speed : 10
     * gradient : 1
     * distance : 20
     * gravity : 80
     * time : 1
     * app_ver : 1.0.0
     */

    private String bracelet_id;
    private String heart_rate;
    private String device_name;
    private String gym_name;
    private String speed;
    private String gradient;
    private String distance;
    private String gravity;
    private String time;
    private String exercise_time;
    private String u_time; //飞鸟单组运动时长
    private boolean report;
    private List<Integer> curve;

    public boolean isReport() {
        return report;
    }

    public void setReport(boolean report) {
        this.report = report;
    }

    public String getExercise_time() {
        return exercise_time;
    }

    public void setExercise_time(String exercise_time) {
        this.exercise_time = exercise_time;
    }

    public void setBracelet_id(String bracelet_id) {
        this.bracelet_id = bracelet_id;
    }

    public String getBracelet_id() {
        return bracelet_id;
    }

    public String getHeart_rate() {
        return heart_rate;
    }

    public void setHeart_rate(String heart_rate) {
        this.heart_rate = heart_rate;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    public String getGym_name() {
        return gym_name;
    }

    public void setGym_name(String gym_name) {
        this.gym_name = gym_name;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getGradient() {
        return gradient;
    }

    public void setGradient(String gradient) {
        this.gradient = gradient;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGravity() {
        return gravity;
    }

    public void setGravity(String gravity) {
        this.gravity = gravity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getU_time() {
        return u_time;
    }

    public void setU_time(String u_time) {
        this.u_time = u_time;
    }

    public List<Integer> getCurve() {
        return curve;
    }

    public void setCurve(List<Integer> curve) {
        this.curve = curve;
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

        if (obj instanceof BleDeviceInfo) {
            BleDeviceInfo other = (BleDeviceInfo) obj;
            //需要比较的字段相等，则这两个对象相等
            if (this.getBracelet_id().equals(other.getBracelet_id())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Object clone() {
        BleDeviceInfo bleDeviceInfo = null;
        try {
            bleDeviceInfo = (BleDeviceInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return bleDeviceInfo;

    }
}
