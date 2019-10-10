package cn.linkfeeling.hankserve;

import android.app.Activity;
import android.content.Context;
import android.os.Looper;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.widget.Toast;

import com.link.feeling.framework.base.BaseApplication;
import com.simple.spiderman.SpiderMan;
import com.tencent.bugly.crashreport.CrashReport;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.Bmob;
import cn.linkfeeling.hankserve.alarm.LinkAlarmManager;
import cn.linkfeeling.hankserve.crash.UnCeHandler;
import cn.linkfeeling.hankserve.manager.FinalDataManager;
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
    private ArrayList<Activity> list = new ArrayList<Activity>();

    private static App app;
    private volatile boolean isStart;
    private volatile int channelsNum;

    public boolean isStart() {
        return isStart;
    }

    public void setStart(boolean start) {
        isStart = start;
    }

    public int getChannelsNum() {
        return channelsNum;
    }

    public void setChannelsNum(int channelsNum) {
        this.channelsNum = channelsNum;
    }

    public App() {
        Thread.setDefaultUncaughtExceptionHandler(new UnCeHandler(this));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        fix();

    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        CrashReport.initCrashReport(getApplicationContext(), "56e15b1a1e", true);
        // SpiderMan.init(this).setTheme(R.style.SpiderManTheme_Light);
        Bmob.initialize(this, "8e77895692321d5403a19faff7202e36");
        initWakeLock();

        initConfig();
        LinkDataManager.getInstance().createLinkData(this);
        FinalDataManager.getInstance().initObject();
        LinkAlarmManager.getInstance().startRemind(this);

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

    public void fix() {
        try {
            Class clazz = Class.forName("java.lang.Daemons$FinalizerWatchdogDaemon");
            Method method = clazz.getSuperclass().getDeclaredMethod("stop");
            method.setAccessible(true);
            Field field = clazz.getDeclaredField("INSTANCE");
            field.setAccessible(true);
            method.invoke(field.get(null));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 16      * Activity关闭时，删除Activity列表中的Activity对象
     */
    public void removeActivity(Activity a) {
        list.remove(a);
    }

    /**
     * 向Activity列表中添加Activity对象
     */
    public void addActivity(Activity a) {
        list.add(a);
    }

    /**
     * 关闭Activity列表中的所有Activity
     */
    public void finishActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
