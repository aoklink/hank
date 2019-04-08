package com.link.feeling.framework.component.net.interceptor;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.link.feeling.framework.utils.data.L;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static com.alibaba.fastjson.util.IOUtils.UTF8;

/**
 * Created on 2019/1/3  15:31
 * chenpan pan.chen@linkfeeling.cn
 *
 * 拦截器，打印日志
 */
@SuppressWarnings("unused")
public final class LoggingInterceptor implements Interceptor {
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        String requestStr = null;
        String responseStr = null;

        Buffer bufferReq = new Buffer();
        if (request.body() != null) {
            request.body().writeTo(bufferReq);
            requestStr = bufferReq.readUtf8();
        }
        long startNs = System.nanoTime();
        Response response = chain.proceed(request);

        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        ResponseBody responseBody = response.body();

        long contentLength = responseBody!=null? responseBody.contentLength():0;
        if (!InterceptorHelper.bodyEncoded(response.headers())) {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset charset = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null) {
                try {
                    charset = contentType.charset(UTF8);
                } catch (UnsupportedCharsetException e) {
                    return response;
                }
            }

            if (!InterceptorHelper.isPlaintext(buffer)) {
                return response;
            }

            if (contentLength != 0) {
                responseStr = buffer.clone().readString(charset);
            }
        }
        if (!TextUtils.isEmpty(responseStr)) {
            L.e(">>>请求URL--" + request.url().toString() + ">>>请求耗时：" + String.valueOf(tookMs));
            if (!TextUtils.isEmpty(requestStr)) {
                String decode = URLDecoder.decode(requestStr, "utf-8");
                String[] split = decode.split("&");
                L.result(JSON.toJSONString(split));
                L.e("------------------------------------༻分༻隔༻符༻---------------------------------------------------------------------------------------------------------------");
            }
            L.result(responseStr);
        }
        return response;
    }
}
