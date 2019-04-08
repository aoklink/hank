package com.link.feeling.framework.component.net.provider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created on 2019/1/7  11:22
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class GsonProvider {

    private final Gson mGson;

    private GsonProvider() {
        mGson = new GsonBuilder()
                .create();
    }

    public static GsonProvider getInstance() {
        return SingleHolder.INSTNACE;
    }

    public Gson getGson() {
        return mGson;
    }

    private static class SingleHolder {
        private final static GsonProvider INSTNACE = new GsonProvider();
    }
}
