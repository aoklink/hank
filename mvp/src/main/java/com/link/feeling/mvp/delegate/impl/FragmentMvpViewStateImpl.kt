package com.link.feeling.mvp.delegate.impl

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.delegate.MvpViewStateDelegateCallback
import com.link.feeling.mvp.presentermanager.PresenterManager
import com.link.feeling.mvp.viewstate.ViewState

/**
 * Created on 2019/1/14  10:41
 * chenpan pan.chen@linkfeeling.cn
 */
class FragmentMvpViewStateImpl<V : MvpView, P : MvpPresenter<V>, VS : ViewState<V>>(
        fragment: Fragment,
        val viewStateDelegateCallback: MvpViewStateDelegateCallback<V, P, VS>,
        keepPresenterDuringScreenOrientationChange: Boolean,
        keepPresenterOnBackstack: Boolean) :
        FragmentMvpDelegateImpl<V, P>(
                fragment,
                viewStateDelegateCallback,
                keepPresenterDuringScreenOrientationChange,
                keepPresenterOnBackstack) {

    var applyViewState: Boolean = false

    override fun onCreate(bundle: Bundle?) {
        // 从缓存中获取 Presenter 或者创建新的
        super.onCreate(bundle)

        viewId?.let {
            val viewState: VS? = PresenterManager.getViewState(fragment.requireActivity(), it)
            viewState?.let {
                viewStateDelegateCallback.setViewState(it)
                if (ActivityMvpViewStateDelegateImpl.DEBUG) {
                    Log.d(ActivityMvpViewStateDelegateImpl.DEBUG_TAG, "根据 View ID：$viewId 获取缓存的 ViewState：$it")
                }
                return
            }
        }

        val viewState: VS = createViewState()
        viewStateDelegateCallback.setViewState(viewState)
    }


//    override fun onStart() {
//        super.onStart()
//
//        if (applyViewState) {
//            val viewState = viewStateDelegateCallback.getViewState()
//            val view = viewStateDelegateCallback.getMvpView()
//
//            viewStateDelegateCallback.setRestoringViewState(true)
//            viewState.apply(view)
//            viewStateDelegateCallback.setRestoringViewState(false)
//
//            if (keepPresenterDuringScreenOrientationChange) {
//                PresenterManager.putViewState(fragment.requireActivity(), checkNotNull(viewId) {
//                    "View ID 应该在 Presenter 缓存的时候生成了，这时候不应该为空"
//                }, viewState)
//            }
//
//            viewStateDelegateCallback.onViewStateInstanceRestored()
//        } else {
//            viewStateDelegateCallback.onNewViewStateInstance()
//        }
//    }

    fun createViewState(): VS {
        val viewState: VS = viewStateDelegateCallback.createViewState()
        if (keepPresenterDuringScreenOrientationChange) {
            PresenterManager.putViewState(fragment.requireActivity(), checkNotNull(viewId) {
                "View ID 应该在 Presenter 缓存的时候生成了，这时候不应该为空"
            }, viewState)
        }

        return viewState
    }

}