package cn.linkfeeling.hankserve.bean;

import java.util.List;

/**
 * @author create by zhangyong
 * @time 2019/11/6
 */
public class WebAccount {


    /**
     * data : ["I7D712"]
     * type : 100
     */

    private int type;
    private List<String> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
