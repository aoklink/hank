package com.link.feeling.mvp.presentermanager

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.v4.util.ArrayMap
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.utils.ContextUtils.toViewId

/**
 * Created on 2019/1/11  16:20
 * chenpan pan.chen@linkfeeling.cn
 */
@Suppress("UNUSED")
object PresenterManager {

    private const val KEY_ACTIVITY_ID = "com.linkfeeling.mvp.presentermanager.PresenterManager.id"

    private const val DEBUG_TAG = "PresenterManager"

    private val ACTIVITY_MAP: ArrayMap<Activity, String> = ArrayMap()

    private val ACTIVITY_SCOPED_CACHE_MAP: ArrayMap<String, ActivityScopedCache> = ArrayMap()

    private val ACTIVITY_LIFECYCLE_CALLBACKS = object : Application.ActivityLifecycleCallbacks {

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityDestroyed(activity: Activity) {

            if (!activity.isChangingConfigurations) {
                val activityId = ACTIVITY_MAP[activity]
                if (activityId != null) {
                    val scopedCache = ACTIVITY_SCOPED_CACHE_MAP[activityId]
                    if (scopedCache != null) {
                        scopedCache.clear()
                        ACTIVITY_SCOPED_CACHE_MAP.remove(activityId)
                    }

                    if (ACTIVITY_SCOPED_CACHE_MAP.isEmpty) {
                        activity.application.unregisterActivityLifecycleCallbacks(this)
                    }
                }
            }

            ACTIVITY_MAP.remove(activity)
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            val activityId = ACTIVITY_MAP[activity]
            if (activityId != null) {
                outState.putString(KEY_ACTIVITY_ID, activityId)
            }
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            val activityId = savedInstanceState?.getString(KEY_ACTIVITY_ID)
            if (activityId != null) {
                ACTIVITY_MAP[activity] = activityId
            }
        }

    }

    /**
     * 根据 activity 获取 activity scoped cache，不存在则创建它
     */
    fun getOrCreateActivityScopedCache(activity: Activity): ActivityScopedCache {

        var activityId = ACTIVITY_MAP[activity]
        if (activityId == null) {
            activityId = activity.toViewId()
            ACTIVITY_MAP[activity] = activityId

            if (ACTIVITY_MAP.size == 1) {
                activity.application.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE_CALLBACKS)
            }
        }

        var activityScopedCache = ACTIVITY_SCOPED_CACHE_MAP[activityId]
        if (activityScopedCache == null) {
            activityScopedCache = ActivityScopedCache()
            ACTIVITY_SCOPED_CACHE_MAP[activityId] = activityScopedCache
        }

        return activityScopedCache
    }

    /**
     * 根据 activity 获取 scoped cache，不存在返回 null
     */
    fun getActivityScope(activity: Activity): ActivityScopedCache? {
        val activityId = ACTIVITY_MAP[activity]
        activityId ?: return null
        return ACTIVITY_SCOPED_CACHE_MAP[activityId]
    }

    /**
     * 根据 activity 和 viewid 获取 presenter，不存在返回 null
     */
    fun <P> getPresenter(activity: Activity, viewId: String): P? {
        val scopedCache = getActivityScope(activity)
        scopedCache ?: return null
        return scopedCache.getPresenter(viewId)
    }

    /**
     * 根据 activity 和 viewid 获取 view state，不存在返回 null
     */
    fun <VS> getViewState(activity: Activity, viewId: String): VS? {
        val scopedCache = getActivityScope(activity)
        scopedCache ?: return null
        return scopedCache.getViewState(viewId)
    }

    /**
     * 根据 activity 和 viewId 设置 presenter
     */
    fun putPresenter(activity: Activity, viewId: String, presenter: MvpPresenter<*>) {
        val scopedCache = getOrCreateActivityScopedCache(activity)
        scopedCache.putPresenter(viewId, presenter)
    }

    /**
     * 根据 activity 和 viewId 设置 viewState
     */
    fun putViewState(activity: Activity, viewId: String, viewState: Any) {
        val scopedCache = getOrCreateActivityScopedCache(activity)
        scopedCache.putViewState(viewId, viewState)
    }

    /**
     * 根据 activity 和 删除 presenter holder
     */
    fun remove(activity: Activity, viewId: String) {
        val scopedCache = getActivityScope(activity)
        scopedCache?.remove(viewId)
    }


}