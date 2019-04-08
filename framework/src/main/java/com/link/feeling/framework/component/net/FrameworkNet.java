package com.link.feeling.framework.component.net;

import com.google.gson.Gson;
import com.link.feeling.framework.component.net.config.ServerConfig;
import com.link.feeling.framework.component.net.provider.GsonProvider;
import com.link.feeling.framework.component.net.provider.LinkOkHttpClient;

import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created on 2019/1/7  11:07
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class FrameworkNet {

    private final OkHttpClient mOkHttpClient;
    private final Retrofit mRetrofit;

    private FrameworkNet() {
        mOkHttpClient = LinkOkHttpClient.okHttpClient();
        mRetrofit = createRetrofit();
    }

    public static FrameworkNet getInstance() {
        return SingleHolder.INSTANCE;
    }

    public OkHttpClient providerOkHttpClient() {
        return mOkHttpClient;
    }

    public Retrofit providerRetrofit() {
        return mRetrofit;
    }

    public Gson providerGson() {
        return GsonProvider.getInstance().getGson();
    }

    private Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ServerConfig.HOST)
                .client(providerOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(GsonProvider.getInstance().getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
    }


    private static class SingleHolder {

        private final static FrameworkNet INSTANCE = new FrameworkNet();

    }

}
