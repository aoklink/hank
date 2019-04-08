package com.link.feeling.framework.base;

import android.app.Application;
import android.content.Context;


import com.link.feeling.framework.component.dao.MyObjectBox;
import com.link.feeling.framework.component.net.config.ServerConfig;
import com.link.feeling.framework.utils.ui.ActivityUtils;

import io.objectbox.BoxStore;
import io.objectbox.android.AndroidObjectBrowser;

/**
 * Created on 2019/1/3  15:06
 * chenpan pan.chen@linkfeeling.cn
 */
public  class BaseApplication extends Application {

    // context
    private static BaseApplication sContext;
    // box
    private static BoxStore sBoxStore;
    // uid
    public static int sUID;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        ActivityUtils.init(this);
        ServerConfig.initEnv();
        initObjectBox();

    }

    public static Context getAppContext() {
        return sContext;
    }

    public static BoxStore getBoxStore() {
        return sBoxStore;
    }

    /**
     * 初始化数据库
     */
    private void initObjectBox() {
        sBoxStore = MyObjectBox.builder().androidContext(this).build();
        new AndroidObjectBrowser(sBoxStore).start(this);
    }

    @Override
    public void onLowMemory() {
        System.gc();
        System.runFinalization();
        System.gc();
        super.onLowMemory();
    }
}
