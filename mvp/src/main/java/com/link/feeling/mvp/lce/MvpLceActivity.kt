package com.link.feeling.mvp.lce

import android.support.annotation.CallSuper
import android.view.View
import com.link.feeling.mvp.MvpActivity
import com.link.feeling.mvp.R
import com.link.feeling.mvp.animator.UIAnimator
import com.link.feeling.mvp.animator.impl.DefaultUIAnimator
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView

/**
 * Created on 2019/1/14  15:38
 * chenpan pan.chen@linkfeeling.cn
 */
abstract class MvpLceActivity<V : MvpView, P : MvpPresenter<V>> : MvpActivity<V, P>() {

    val contentView: View by lazy(LazyThreadSafetyMode.NONE) {
        requireNotNull(createContentView()) {
            "contentView 不能为空"
        }
    }

    val loadingView: View by lazy(LazyThreadSafetyMode.NONE) {
        requireNotNull(createLoadingView()) {
            "loadingView 不能为空"
        }
    }

    val errorView: View by lazy(LazyThreadSafetyMode.NONE) {
        requireNotNull(createErrorView()) {
            "errorView 不能为空"
        }
    }

    val animator: UIAnimator by lazy(LazyThreadSafetyMode.NONE) {
        createUIAnimator()
    }

    @CallSuper
    override fun onContentChanged() {
        super.onContentChanged()
        errorView.setOnClickListener {
            onErrorViewClicked()
        }
    }


    /**
     * 展示加载视图
     */
    override fun showLoading() {
        animator.showLoading(loadingView, contentView, errorView)
    }

    /**
     * 展示内容视图
     */
    override fun showContent() {
        animator.showContent(loadingView, contentView, errorView)
    }

    /**
     * 展示错误视图
     */
    override fun showError(throwable: Throwable) {
        animator.showErrorView(loadingView, contentView, errorView)
    }

    /**
     * 返回视图切换使用的动画器
     */
    fun createUIAnimator(): UIAnimator = DefaultUIAnimator()

    /**
     * 返回正常内容视图，默认为 R.id.contentView
     */
    fun createContentView(): View = findViewById(R.id.mvp_contentView)

    /**
     * 返回错误时展示的视图，默认为 R.id.errorView
     */
    fun createErrorView(): View = findViewById(R.id.mvp_errorView)

    /**
     * 返回加载时展示的视图，默认为 R.id.loadingView
     */
    fun createLoadingView(): View = findViewById(R.id.mvp_loadingView)

    /**
     * 点击错误视图
     */
    open fun onErrorViewClicked() {

    }


}