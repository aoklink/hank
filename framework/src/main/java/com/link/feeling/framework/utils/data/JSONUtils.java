package com.link.feeling.framework.utils.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.asm.Type;
import com.link.feeling.framework.component.net.NetResult;

/**
 * Created on 2019/1/17  11:09
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class JSONUtils {

    public static String toJSONString(Object object) {
        return JSON.toJSONString(object);
    }

    public static String toJSONString(Object o, Type type) {
        return JSON.toJSONString(o);
    }

    public JSONUtils() {
        super();
    }

    public static <T> T parseObject(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * 将Json字符串转换成NetResult
     * @param json
     * @param <T>
     * @return
     */
    public static <T> NetResult<T> parseNetResult(String json, Class<T> clazz) {
        return JSON.parseObject(json,  new TypeReference<NetResult<T>>(clazz){}.getType());
    }

}
