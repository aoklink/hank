package com.link.feeling.mvp.delegate.impl

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.link.feeling.mvp.BuildConfig
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.delegate.ActivityMvpDelegate
import com.link.feeling.mvp.delegate.MvpDelegateCallback
import com.link.feeling.mvp.presentermanager.PresenterManager
import com.link.feeling.mvp.utils.ContextUtils.toViewId

/**
 * Created on 2019/1/11  16:12
 * chenpan pan.chen@linkfeeling.cn
 */
open class ActivityMvpDelegateImpl<V : MvpView, P : MvpPresenter<V>>(val activity: Activity,
                                                                     val delegateCallback: MvpDelegateCallback<V, P>,
                                                                     var keepPresenterInstance: Boolean) : ActivityMvpDelegate<V, P> {


    companion object {
        const val KEY_VIEW_ID = "com.ultimavip.mvp.delegate.impl.ActivityMvpDelegateImpl.id"
        val DEBUG = BuildConfig.DEBUG
        const val DEBUG_TAG = "ActivityMvpDelegateImpl"
    }

    protected var viewId: String? = null

    override fun onCreate(bundle: Bundle?) {

        var presenter: P? = null

        if (bundle != null && keepPresenterInstance) {

            viewId = bundle.getString(KEY_VIEW_ID)

            if (DEBUG) {
                Log.d(DEBUG_TAG, "从 ${delegateCallback.getMvpView()} 获取缓存的 View ID：$viewId")
            }

            val finalViewId = viewId

            if (finalViewId != null) {
                presenter = PresenterManager.getPresenter(activity, finalViewId)
            }

            if (presenter != null) {
                if (DEBUG) {
                    Log.d(DEBUG_TAG, "复用 ${delegateCallback.getMvpView()} 的 presenter：$presenter")
                }
            } else {
                presenter = createViewIdAndCreatePresenter()
                if (DEBUG) {
                    Log.d(DEBUG_TAG, "获取缓存的 Presenter 失败，获取新的 Presenter：$presenter")
                }
            }

        } else {
            presenter = createViewIdAndCreatePresenter()
            if (DEBUG) {
                Log.d(DEBUG_TAG, "不存在缓存，直接获取新的 Presenter：$presenter")
            }
        }

        delegateCallback.setPresenter(presenter)
        delegateCallback.getPresenter().attachView(delegateCallback.getMvpView())

        if (DEBUG) {
            Log.d(DEBUG_TAG, "$presenter attachView ${delegateCallback.getMvpView()}")
        }
    }


    override fun onDestroy() {
        val retainPresenterInstance = retainPresenterInstance(keepPresenterInstance, activity)
        delegateCallback.getPresenter().detachView()
        if (!retainPresenterInstance) {
            delegateCallback.getPresenter().destroy()
        }
        if (!retainPresenterInstance) {
            // 清除缓存
            viewId?.let { PresenterManager.remove(activity, it) }
        }
        if (DEBUG) {
            if (retainPresenterInstance) {
                Log.d(DEBUG_TAG, "View：${delegateCallback.getMvpView()} 只是临时分离，" +
                        "缓存 Presenter 实例：${delegateCallback.getPresenter()}")
            } else {
                Log.d(DEBUG_TAG, "不需要保存 Presenter 实例：${delegateCallback.getPresenter()}")
            }
        }
    }

    override fun onPause() {
    }

    override fun onResume() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onRestart() {
    }

    override fun onContentChanged() {
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (keepPresenterInstance) {
            outState?.let {
                it.putString(KEY_VIEW_ID, viewId)
                if (DEBUG) {
                    Log.d(DEBUG_TAG, "保存 View：${delegateCallback.getMvpView()} 的 View ID：$viewId" +
                            " 到 Bundle")
                }
            }
        }
    }

    override fun onPostCreate(bundle: Bundle?) {
    }

    /**
     * 创建 View ID 同时创建 Presenter，保存到 PresenterManager
     */
    private fun createViewIdAndCreatePresenter(): P {
        val presenter: P = delegateCallback.createPresenter()
        if (keepPresenterInstance) {
            viewId = activity.toViewId()
            PresenterManager.putPresenter(activity, viewId!!, presenter)
        }
        return presenter
    }

    /**
     * 是否需要保存 Presenter 实例
     */
    private fun retainPresenterInstance(keepPresenterInstance: Boolean, activity: Activity): Boolean {
        return keepPresenterInstance && (activity.isChangingConfigurations || !activity.isFinishing)
    }
}