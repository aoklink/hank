package com.link.feeling.framework.component.net.json;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.link.feeling.framework.component.net.NetResult;
import com.link.feeling.framework.component.net.config.HttpErrorHandler;
import com.link.feeling.framework.component.net.exception.NetException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Function;
import okhttp3.FormBody;
import okhttp3.Response;

/**
 * Created on 2019/1/17  11:38
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class ResponseParse {
    private ResponseParse() {
    }

    public static <T> T convertBean(Response response, Class<T> clazz) throws Exception {
        String responseString = response.body().string();
        JSONObject dataObject = JSON.parseObject(responseString);
        NetResult<String> netResult = parseNetResult(dataObject);
        if (!HttpErrorHandler.isSuccess(netResult)) {
            throw new NetException(netResult);
        }
        String dataString = dataObject.getString("data");
        return JSON.parseObject(dataString, clazz);
    }

    public static <T> List<T> convertNestingListBean(Response response, Class<T> clazz) throws Exception {
        String responseString = response.body().string();
        JSONObject dataObject = JSON.parseObject(responseString);
        NetResult<String> netResult = parseNetResult(dataObject);
        if (!HttpErrorHandler.isSuccess(netResult)) {
            throw new NetException(netResult);
        }
        if (TextUtils.isEmpty(dataObject.getJSONObject("data").getString("list"))) {
            return Collections.emptyList();
        }
        String listString = dataObject.getJSONObject("data").getString("list");
        return JSON.parseArray(listString, clazz);
    }

    public static <T> List<T> convertListBean(Response response, Class<T> clazz) throws Exception {
        String responseString = response.body().string();
        JSONObject dataObject = JSON.parseObject(responseString);
        NetResult<String> netResult = parseNetResult(dataObject);
        if (!HttpErrorHandler.isSuccess(netResult)) {
            throw new NetException(netResult);
        }
        String dataString = dataObject.getString("data");
        return JSON.parseArray(dataString, clazz);
    }

    public static void checkResponse(Response response) throws Exception {
        String responseString = response.body().string();
        JSONObject dataObject = JSON.parseObject(responseString);
        NetResult<String> netResult = parseNetResult(dataObject);
        if (!HttpErrorHandler.isSuccess(netResult)) {
            throw new NetException(netResult);
        }
    }

    public static FormBody body(Map<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            builder.add(
                    entry.getKey(),
                    entry.getValue()
            );
        }
        return builder.build();
    }


    public static NetResult<String> parseNetResult(JSONObject jsonObject) throws Exception {
        NetResult<String> result = new NetResult<>();
        result.code = jsonObject.getString("code");
        result.msg = jsonObject.getString("msg");
        result.data = jsonObject.getString("data");
        return result;
    }

    public static void handleNetResult(@NonNull NetResult netResult) throws Exception {
        if (!HttpErrorHandler.isSuccess(netResult)) {
            throw new NetException(netResult);
        }
    }

    public static <T> Function<NetResult<T>, T> mapFun() {
        return tNetResult -> {
            handleNetResult(tNetResult);
            return tNetResult.data;
        };
    }

    public static Function<NetResult<?>, CompletableSource> completableFun() {
        return emptyBeanNetResult -> {
            handleNetResult(emptyBeanNetResult);
            return Completable.complete();
        };
    }
}
