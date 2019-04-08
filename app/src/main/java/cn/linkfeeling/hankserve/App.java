package cn.linkfeeling.hankserve;

import android.app.Application;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import java.io.IOException;
import java.net.ServerSocket;

import cn.linkfeeling.hankserve.nsd.NsdManagers;

/**
 * @author create by zhangyong
 * @time 2019/3/25
 */
public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NsdManagers.createNsdInfo(this);


    }


}
