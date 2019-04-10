package cn.linkfeeling.hankserve;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.simple.spiderman.SpiderMan;

import java.util.concurrent.TimeUnit;

import cn.linkfeeling.link_websocket.Config;
import cn.linkfeeling.link_websocket.RxWebSocket;
import okhttp3.OkHttpClient;

/**
 * @author create by zhangyong
 * @time 2019/3/25
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SpiderMan.init(this)
                .setTheme(R.style.SpiderManTheme_Light);

        // NsdManagers.createNsdInfo(this);
        initConfig();

    }

    private void initConfig() {
        //init config 在使用RxWebSocket之前设置即可，推荐在application里初始化
        Config config = new Config.Builder()
                .setShowLog(true)           //show  log
                .setClient(new OkHttpClient().newBuilder().pingInterval(5, TimeUnit.SECONDS).build())   //if you want to set your okhttpClient
                .setShowLog(true, "link-socket_server")
                .setReconnectInterval(2, TimeUnit.SECONDS)  //set reconnect interval
                .build();
        RxWebSocket.setConfig(config);
    }


}
