package com.link.feeling.framework.component.net;

import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.annotations.JsonAdapter;
import com.link.feeling.framework.component.net.json.NetResultJsonDeserializer;
import com.link.feeling.framework.utils.data.JSONUtils;

/**
 * Created on 2019/1/17  10:49
 * chenpan pan.chen@linkfeeling.cn
 */
@Keep
@JsonAdapter(value = NetResultJsonDeserializer.class)
public class NetResult<T> {

    public String code;

    // 有错误消息显示在这里
    public String msg;

    // 原始数据类型
    public T data;

    /**
     * 将data直接转化为特定的been
     * @param clazz
     * @param <T>
     * @return
     */
    public @Nullable <T> T parseData(Class<T> clazz) {
        return !isNullData() ? JSONUtils.parseObject(data.toString(), clazz) : null;
    }

    public boolean isSuccess() {
        return "200".equals(code);
    }

    /**
     * 判断实体是否为空
     * @return
     */
    public boolean isNullData() {
        return data == null || TextUtils.isEmpty(data.toString()) || "{}".equals(data) || "{ }".equals(data) || "null".equals(data);
    }
}
