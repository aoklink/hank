package com.link.feeling.mvp.delegate

import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.viewstate.ViewState

/**
 * Created on 2019/1/11  16:08
 * chenpan pan.chen@linkfeeling.cn
 */
interface MvpViewStateDelegateCallback<V : MvpView, P : MvpPresenter<V>, VS : ViewState<V>> : MvpDelegateCallback<V, P> {

    /**
     * 创建新的 ViewState
     */
    fun createViewState(): VS

    /**
     * 获取当前需要保存的 ViewState
     */
    fun getViewState(): VS

    /**
     * 设置 ViewState
     */
    fun setViewState(viewState: VS)

    /**
     * 设置是否正处于恢复 ViewState
     */
    fun setRestoringViewState(restoringViewState: Boolean)

    /**
     * 返回是否正处于恢复 ViewState
     */
    fun isRestoringViewState(): Boolean

    /**
     * 恢复 ViewState 解释时的回调
     */
    fun onViewStateInstanceRestored()

    /**
     * 创建新的 ViewState 时的回调
     */
    fun onNewViewStateInstance(viewState: VS)

}