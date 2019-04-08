package com.link.feeling.mvp.delegate.impl

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.link.feeling.mvp.BuildConfig
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.delegate.MvpViewStateDelegateCallback
import com.link.feeling.mvp.presentermanager.PresenterManager
import com.link.feeling.mvp.viewstate.ViewState

/**
 * Created on 2019/1/14  9:58
 * chenpan pan.chen@linkfeeling.cn
 */
class ActivityMvpViewStateDelegateImpl<V : MvpView, P : MvpPresenter<V>, VS : ViewState<V>>(
        activity: Activity,
        val viewStateDelegateCallback: MvpViewStateDelegateCallback<V, P, VS>,
        keepPresenterInstance: Boolean
) : ActivityMvpDelegateImpl<V, P>(activity, viewStateDelegateCallback, keepPresenterInstance) {


    companion object {
        val DEBUG = BuildConfig.DEBUG
        const val DEBUG_TAG = "ActivityMvpViewStateDel"
    }

    override fun onPostCreate(bundle: Bundle?) {
        super.onPostCreate(bundle)

        viewId?.let {
            val viewState: VS? = PresenterManager.getViewState(activity, it)
            viewState?.let {
                setViewState(it, true)
                if (DEBUG) {
                    Log.d(DEBUG_TAG, "根据 View ID：$viewId 获取缓存的 ViewState：$it")
                }
                return
            }
        }

        val viewState: VS = viewStateDelegateCallback.createViewState()
        if (keepPresenterInstance) {
            PresenterManager.putViewState(activity, checkNotNull(viewId) {
                "当前需要保持 Presenter 实例，但是 View Id 为空"
            }, viewState)
        }
        setViewState(viewState, false)
        if (DEBUG) {
            Log.d(DEBUG_TAG, "创建新的 ViewState 实例：$viewState 从 View：${viewStateDelegateCallback.getMvpView()}")
        }
        viewStateDelegateCallback.onNewViewStateInstance(viewState)
    }

    private fun setViewState(viewSTate: VS, applyViewState: Boolean) {
        viewStateDelegateCallback.setViewState(viewSTate)
        if (applyViewState) {
            viewStateDelegateCallback.setRestoringViewState(true)
            viewStateDelegateCallback.getViewState().apply(viewStateDelegateCallback.getMvpView())
            viewStateDelegateCallback.setRestoringViewState(false)
            viewStateDelegateCallback.onViewStateInstanceRestored()
        }
    }
}