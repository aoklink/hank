package com.link.feeling.framework.component.net.json;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.link.feeling.framework.component.net.NetResult;
import com.link.feeling.framework.component.net.provider.GsonProvider;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2019/1/17  10:51
 * chenpan pan.chen@linkfeeling.cn
 */
public class NetResultJsonDeserializer implements JsonDeserializer<NetResult<Object>> {

    @Override
    public NetResult<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        NetResult<Object> netResult = new NetResult<>();
        netResult.msg = "";
        netResult.code = "";
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            if (effective(jsonObject, "msg") && jsonObject.get("msg").isJsonPrimitive()) {
                netResult.msg = jsonObject.get("msg").getAsString();
            }
            if (effective(jsonObject, "code") && jsonObject.get("code").isJsonPrimitive()) {
                netResult.code = jsonObject.get("code").getAsString();
            }
            // 解析 data
            ParameterizedType parameterizedType = (ParameterizedType) typeOfT;
            Type type = parameterizedType.getActualTypeArguments()[0];
            if (!effective(jsonObject, "data")) {
                // data 为空
                if (typeIsList(type)) {
                    netResult.data = Collections.emptyList();
                }
            } else {
                JsonElement dataJson = jsonObject.get("data");
                // 这里只处理两种情况
                if (typeIsList(type)) {
                    // 如果我们需要获取一个列表
                    if (dataJson.isJsonArray()) {
                        netResult.data = context.deserialize(dataJson, type);
                    } else {
                        if (dataJson.isJsonPrimitive() && !TextUtils.isEmpty(dataJson.getAsString())) {
                            Gson gson = GsonProvider.getInstance().getGson();
                            try {
                                netResult.data = gson.fromJson(dataJson.getAsString(), type);
                            } catch (JsonSyntaxException ignore) {
                                netResult.data = Collections.emptyList();
                            }
                        } else {
                            netResult.data = Collections.emptyList();
                        }
                    }
                } else {
                    // 默认处理
                    netResult.data = context.deserialize(dataJson, type);
                }
            }
        }
        return netResult;
    }

    private boolean effective(JsonObject json, String memberName) {
        return json.has(memberName) && !json.get(memberName).isJsonNull();
    }

    private boolean typeIsList(Type type) {
        return type instanceof ParameterizedType && ((ParameterizedType) type).getRawType().equals(List.class);
    }

}

