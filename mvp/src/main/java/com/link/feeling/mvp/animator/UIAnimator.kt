package com.link.feeling.mvp.animator

import android.view.View

/**
 * Created on 2019/1/14  11:18
 * chenpan pan.chen@linkfeeling.cn
 */
interface UIAnimator {

    /**
     * 展示加载视图
     */
    fun showLoading(loadingView: View, contentView: View, errorView: View)


    /**
     * 展示错误视图
     */
    fun showErrorView(loadingView: View, contentView: View, errorView: View)


    /**
     * 展示内容视图
     */
    fun showContent(loadingView: View, contentView: View, errorView: View)

}