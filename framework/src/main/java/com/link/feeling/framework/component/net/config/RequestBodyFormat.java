package com.link.feeling.framework.component.net.config;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.link.feeling.framework.KeysConstants;
import com.link.feeling.framework.utils.data.DeviceUtils;
import com.link.feeling.framework.utils.data.MD5Utils;
import com.link.feeling.framework.utils.data.SPUtils;

import java.io.IOException;
import java.util.Map;

import okhttp3.RequestBody;
import okio.Buffer;

/**
 * Created on 2019/1/28  10:04
 * chenpan pan.chen@linkfeeling.cn
 */
public class RequestBodyFormat {
    public static String getParams(RequestBody requestBody) {
        String requestStr;
        Buffer bufferReq = new Buffer();
        if (requestBody != null) {
            try {
                requestBody.writeTo(bufferReq);
                requestStr = bufferReq.readUtf8();
                Gson gson = new Gson();
                Map<String, String> map = gson.fromJson(requestStr, Map.class);
//                map.put(KeysConstants.UID, SPUtils.getUID());
//                map.put(KeysConstants.SESSION_ID, SPUtils.getSessionId());
//                map.put(KeysConstants.USER_TYPE, SPUtils.getUserType());
                map.put(KeysConstants.REQUEST_TIME, String.valueOf(System.currentTimeMillis()));
//                map.put(KeysConstants.PLATFORM, KeysConstants.ANDROID);
//                map.put(KeysConstants.NETWORK, DeviceUtils.getNetWorkType());
//                map.put(KeysConstants.PRODUCT_ID, KeysConstants.LINK_FEELING);
//                map.put(KeysConstants.TK, MD5Utils.to32Md5(KeysConstants.LINK_FEELING+":"+SPUtils.getUserType()+":"+map.get(KeysConstants.REQUEST_TIME)));
                map.put(KeysConstants.APP_VERSION, DeviceUtils.getVersionName());

                return JSON.toJSONString(map);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
