package com.link.feeling.framework.utils.ui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

/**
 * Created on 2019/1/8  11:12
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class ActivityUtils {

    private ActivityUtils() {
        throw new UnsupportedOperationException("tools class can not call constructors");
    }

    private final static List<Activity> ACTIVITY_LIST = new LinkedList<>();

    private final static Application.ActivityLifecycleCallbacks ACTIVITY_LIFECYCLE_CALLBACKS = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            ACTIVITY_LIST.add(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            ACTIVITY_LIST.remove(activity);
        }
    };

    public static void init(Application application) {
        application.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE_CALLBACKS);
    }

    /**
     * 获取当前栈中最前面的 Activity
     *
     * @return activity
     */
    @Nullable
    public static Activity getLastedActivity() {
        if (ACTIVITY_LIST.isEmpty()) {
            return null;
        }
        return ACTIVITY_LIST.get(ACTIVITY_LIST.size() - 1);
    }


    /**
     * 当前栈中是否存在 Activity 实例
     *
     * @param clazz activity class
     * @return 是否存在
     */
    public static <T extends Activity> boolean existActivity(Class<T> clazz) {
        if (ACTIVITY_LIST.isEmpty()) {
            return false;
        }
        for (Activity activity : ACTIVITY_LIST) {
            if (activity.isFinishing()) {
                continue;
            }
            if (activity.getClass().getName().equals(clazz.getName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取当前 Activity 的 ContentView
     *
     * @param activity activity
     * @return contentview
     */
    @Nullable
    public static View getActivityContentView(Activity activity) {
        if (activity.getWindow() != null && activity.getWindow().getDecorView() != null &&
                activity.getWindow().getDecorView().findViewById(android.R.id.content) != null) {
            return activity.getWindow().getDecorView().findViewById(android.R.id.content);
        }
        return null;
    }
}
