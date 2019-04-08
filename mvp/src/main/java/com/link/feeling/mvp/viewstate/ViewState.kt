package com.link.feeling.mvp.viewstate

import com.link.feeling.mvp.common.MvpView

/**
 * Created on 2019/1/11  16:09
 * chenpan pan.chen@linkfeeling.cn
 */
interface ViewState<V : MvpView> {

    /**
     * 将当前 ViewState 应用到给定的 View
     */
    fun apply(view: V)

}