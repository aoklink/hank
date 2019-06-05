package com.link.feeling.framework.component.net.provider;

import com.link.feeling.framework.component.net.interceptor.CommonParamsInterceptor;
import com.link.feeling.framework.component.net.interceptor.LoggingInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * Created on 2019/1/4  18:56
 * chenpan pan.chen@linkfeeling.cn
 *
 * Client封装
 */
@SuppressWarnings("unused")
public final class LinkOkHttpClient {

    private static OkHttpClient sClient;
    /**
     * 获取一个OkHttpClient对象
     *
     * @return this
     */
    public static OkHttpClient okHttpClient() {
        if (sClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true);
            builder.addNetworkInterceptor(new LoggingInterceptor());
            builder.addInterceptor(new CommonParamsInterceptor());
            sClient = builder.build();
        }
        return sClient;
    }
}
