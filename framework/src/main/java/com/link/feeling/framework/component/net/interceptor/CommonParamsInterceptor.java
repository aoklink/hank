package com.link.feeling.framework.component.net.interceptor;

import com.link.feeling.framework.component.net.config.RequestBodyFormat;
import com.link.feeling.framework.component.net.config.ServerConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.link.feeling.framework.KeysConstants.APPLICATION;
import static com.link.feeling.framework.KeysConstants.CONTENT_TYPE;
import static com.link.feeling.framework.KeysConstants.USER_AGENT;

/**
 * Created on 2019/1/7  10:50
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class CommonParamsInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = addCommonParamsIfNeed(chain);
        return chain.proceed(request);
    }

    /**
     * 添加公用参数,若是需要。暂时，若有sign，证明已经添加过公用参数
     * 应用暂时post请求添加公用参数
     *
     * @param chain
     */
    private Request addCommonParamsIfNeed(Chain chain) {
        Request request = chain.request();
        Request.Builder sourceRequest = request.newBuilder();
        RequestBody requestBody = request.body();
        request = sourceRequest.removeHeader(USER_AGENT).addHeader(CONTENT_TYPE, APPLICATION)
                .post(RequestBody.create(ServerConfig.MEDIA_TYPE, RequestBodyFormat.getParams(requestBody))).build();
        return request;

    }
}
