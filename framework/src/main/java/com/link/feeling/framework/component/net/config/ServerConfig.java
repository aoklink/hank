package com.link.feeling.framework.component.net.config;

import okhttp3.MediaType;

/**
 * Created on 2019/1/7  11:00
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class ServerConfig {

    public static String HOST;
    public static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    /**
     * Application.create 中 调用
     *
     * 拓展类...
     */
    public static void initEnv() {
        HOST = "https://dev.linkfeeling.cn/api/";
    }


}
