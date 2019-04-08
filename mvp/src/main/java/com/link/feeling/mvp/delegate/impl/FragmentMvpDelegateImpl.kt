package com.link.feeling.mvp.delegate.impl

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.View
import com.link.feeling.mvp.BuildConfig
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.delegate.FragmentMvpDelegate
import com.link.feeling.mvp.delegate.MvpDelegateCallback
import com.link.feeling.mvp.presentermanager.PresenterManager
import com.link.feeling.mvp.utils.ContextUtils.toViewId
import com.link.feeling.mvp.utils.FragmentUtils.isInBackStack

/**
 * Created on 2019/1/14  10:29
 * chenpan pan.chen@linkfeeling.cn
 */
open class FragmentMvpDelegateImpl<V : MvpView, P : MvpPresenter<V>>(val fragment: Fragment,
                                                                     val delegateCallback: MvpDelegateCallback<V, P>,
                                                                     val keepPresenterDuringScreenOrientationChange: Boolean,
                                                                     val keepPresenterOnBackstack: Boolean) : FragmentMvpDelegate<V, P> {

    companion object {
        val DEBUG = BuildConfig.DEBUG
        const val DEBUG_TAG = "FragmentMvpDelegateImpl"
        const val KEY_VIEW_ID = "com.ultimavip.mvp.delegate.impl.FragmentMvpDelegateImpl.id"
    }

    var viewId: String? = null
    var onViewCreateCalled = false

    init {
        if (!keepPresenterDuringScreenOrientationChange && keepPresenterOnBackstack) {
            throw IllegalArgumentException("启用 keepPresenterOnBackstack 必须也 ke启用epPresenterDuringScreenOrientationChange")
        }
    }

    override fun onActivityCreated(bundle: Bundle?) {
    }

    override fun onAttach(context: Context) {
    }

    override fun onCreate(bundle: Bundle?) {

        var presenter: P? = null

        if (keepPresenterDuringScreenOrientationChange && bundle != null) {

            viewId = bundle.getString(KEY_VIEW_ID)

            val finalViewId = viewId

            if (DEBUG) {
                Log.d(DEBUG_TAG, "从 ${delegateCallback.getMvpView()} 获取缓存的 View ID：$viewId")
            }

            if (finalViewId != null) {
                presenter = PresenterManager.getPresenter(fragment.requireActivity(), finalViewId)
            }
        }

        if (presenter != null) {
            if (DEBUG) {
                Log.d(DEBUG_TAG, "复用 ${delegateCallback.getMvpView()} 的 presenter：$presenter")
            }
        } else {
            presenter = createViewIdAndCreatePresenter()
            if (DEBUG) {
                Log.d(DEBUG_TAG, "不存在缓存，直接获取新的 Presenter：$presenter")
            }
        }

        delegateCallback.setPresenter(presenter)
    }

    override fun onViewCreated(view: View, bundle: Bundle?) {
        delegateCallback.getPresenter().attachView(delegateCallback.getMvpView())
        onViewCreateCalled = true
    }

    override fun onStart() {
        check(onViewCreateCalled) {
            "请确保 onViewCreated 方法被调用"
        }
    }

    override fun onResume() {
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        if (bundle != null && (keepPresenterDuringScreenOrientationChange || keepPresenterOnBackstack)) {

            bundle.putString(KEY_VIEW_ID, viewId)

            if (DEBUG) {
                Log.d(DEBUG_TAG, "保存 View ID：$viewId 到 View：${delegateCallback.getMvpView()} 的 Bundle")
            }


        }
    }

    override fun onPause() {
    }

    override fun onStop() {
    }


    override fun onDestroyView() {
        onViewCreateCalled = false
        delegateCallback.getPresenter().detachView()

        if (DEBUG) {
            Log.d(DEBUG_TAG, "View：${delegateCallback.getMvpView()} " +
                    " destroyView Presenter：${delegateCallback.getPresenter()}")
        }
    }

    override fun onDestroy() {
        val retainPresenterInstance = retainPresenterInstance(fragment.activity!!, fragment, keepPresenterDuringScreenOrientationChange,
                keepPresenterOnBackstack)
        if (!retainPresenterInstance) {
            delegateCallback.getPresenter().destroy()
            if (DEBUG) {
                Log.d(DEBUG_TAG, "不需要保存 Presenter 实例：${delegateCallback.getPresenter()}")
            }
            viewId?.let {
                PresenterManager.remove(fragment.activity!!, it)
            }
        }
    }

    override fun onDetach() {
    }

    /**
     * 是否保存 Fragment Presenter 实例
     */
    private fun retainPresenterInstance(activity: Activity, fragment: Fragment,
                                        keepPresenterInstanceDuringScreenOrientationChanges: Boolean,
                                        keepPresenterOnBackstack: Boolean): Boolean {
        if (activity.isChangingConfigurations) {
            // 当前 Activity 处于配置发生变化中
            return keepPresenterInstanceDuringScreenOrientationChanges
        }

        if (activity.isFinishing) {
            return false
        }

        if (keepPresenterOnBackstack && fragment.isInBackStack()) {
            return true
        }

        return !fragment.isRemoving
    }

    /**
     * 创建 View ID 和 Presenter，并缓存
     */
    private fun createViewIdAndCreatePresenter(): P {
        val presenter = delegateCallback.createPresenter()
        if (keepPresenterDuringScreenOrientationChange) {
            viewId = fragment.context!!.toViewId()
            PresenterManager.putPresenter(fragment.activity!!, viewId!!, presenter)
        }
        return presenter
    }

}