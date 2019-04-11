package cn.linkfeeling.hankserve;

import android.app.Application;
import android.content.Context;
import android.os.PowerManager;
import android.support.multidex.MultiDex;

import com.link.feeling.framework.base.BaseApplication;
import com.simple.spiderman.SpiderMan;

import java.util.concurrent.TimeUnit;

import cn.linkfeeling.hankserve.manager.LinkDataManager;
import cn.linkfeeling.hankserve.udp.UDPBroadcast;
import cn.linkfeeling.link_websocket.Config;
import cn.linkfeeling.link_websocket.RxWebSocket;
import okhttp3.OkHttpClient;

/**
 * @author create by zhangyong
 * @time 2019/3/25
 */
public class App extends BaseApplication {

    private static App app;
    private volatile boolean isSatrt;

    public boolean isSatrt() {
        return isSatrt;
    }

    public void setSatrt(boolean satrt) {
        isSatrt = satrt;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);

    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        SpiderMan.init(this).setTheme(R.style.SpiderManTheme_Light);
        initWakeLock();
        UDPBroadcast.udpBroadcast(this);
        initConfig();
        LinkDataManager.getInstance().createLinkData();

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


    public static App getApplication() {
        return app;
    }


    /**
     * 保持cpu唤醒
     */
    private void initWakeLock() {

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // 创建唤醒锁
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "hank:link-feeling");

        // 获得唤醒锁
        wakeLock.acquire();

    }
}
