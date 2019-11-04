package com.link.feeling.framework.bean;

import com.link.feeling.framework.KeysConstants;

/**
 * Created on 2019/10/30  10:54
 * chenpan pan.chen@linkfeeling.cn
 */
public final class MqttRequest {

    private int type;
    private String gym_id = KeysConstants.GYM;

    public MqttRequest(int type) {
        this.type = type;
    }

    public String getGym_id() {
        return gym_id == null ? "" : gym_id;
    }

    public void setGym_id(String gym_id) {
        this.gym_id = gym_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
