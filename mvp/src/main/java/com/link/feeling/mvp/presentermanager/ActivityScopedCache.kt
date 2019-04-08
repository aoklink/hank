package com.link.feeling.mvp.presentermanager

import android.util.ArrayMap
import com.link.feeling.mvp.common.MvpPresenter

/**
 * Created on 2019/1/11  16:21
 * chenpan pan.chen@linkfeeling.cn
 */
@Suppress("UNUSED")
class ActivityScopedCache {

    private val presenterMap: ArrayMap<String, PresenterHolder> = ArrayMap()

    /**
     * 清理缓存 map
     */
    fun clear() {
        presenterMap.clear()
    }


    /**
     * 根据 viewId 获取 presenter，不存在返回 null
     */
    fun <P> getPresenter(viewId: String): P? {
        val presenterHolder: PresenterHolder = presenterMap[viewId] ?: return null
        @Suppress("UNCHECKED_CAST")
        return presenterHolder.presenter as P
    }

    /**
     * 根据 viewId 获取 ViewState，不存在返回 null
     */
    fun <VS> getViewState(viewId: String): VS? {
        val presenterHolder: PresenterHolder = presenterMap[viewId] ?: return null
        @Suppress("UNCHECKED_CAST")
        return presenterHolder.viewState as VS
    }

    /**
     * 根据 viewId 保存 presenter
     */
    fun putPresenter(viewId: String, presenter: MvpPresenter<*>) {
        var presenterHolder = presenterMap[viewId]
        if (presenterHolder == null) {
            presenterHolder = PresenterHolder(presenter, null)
        } else {
            presenterHolder.presenter = presenter
        }
        presenterMap[viewId] = presenterHolder
    }

    /**
     * 根据 viewId 保存 view state
     */
    fun putViewState(viewId: String, viewState: Any) {
        var presenterHolder = presenterMap[viewId]
        if (presenterHolder == null) {
            presenterHolder = PresenterHolder(null, viewState)
        } else {
            presenterHolder.viewState = viewState
        }
        presenterMap[viewId] = presenterHolder
    }

    /**
     * 根据 viewId 删除 presenter holder
     */
    fun remove(viewId: String) {
        presenterMap[viewId] = null
    }


    private data class PresenterHolder(var presenter: MvpPresenter<*>?, var viewState: Any?)

}