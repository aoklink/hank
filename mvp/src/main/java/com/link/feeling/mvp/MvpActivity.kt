package com.link.feeling.mvp

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatActivity
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.delegate.ActivityMvpDelegate
import com.link.feeling.mvp.delegate.MvpDelegateCallback
import com.link.feeling.mvp.delegate.impl.ActivityMvpDelegateImpl
import kotlin.LazyThreadSafetyMode.NONE

/**
 * Created on 2019/1/11  14:45
 * chenpan pan.chen@linkfeeling.cn
 */
abstract class MvpActivity<V : MvpView, P : MvpPresenter<V>> : AppCompatActivity(), MvpView, MvpDelegateCallback<V, P> {
    private var mvpPresenter: P? = null

    private val mvpDelegate: ActivityMvpDelegate<V, P> by lazy(NONE) {
        ActivityMvpDelegateImpl(this, this, true)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mvpDelegate.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onContentChanged() {
        super.onContentChanged()
        mvpDelegate.onContentChanged()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        mvpDelegate.onDestroy()
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mvpDelegate.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        mvpDelegate.onPause()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        mvpDelegate.onResume()
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        mvpDelegate.onStart()
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        mvpDelegate.onStop()
    }

    @CallSuper
    override fun onRestart() {
        super.onRestart()
        mvpDelegate.onRestart()
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        mvpDelegate.onPostCreate(savedInstanceState)
    }

    @CallSuper
    override fun setPresenter(presenter: P) {
        mvpPresenter = presenter
    }


    override fun getPresenter(): P = checkNotNull(mvpPresenter) {
        "Presenter 为空，请先调用 setPresenter"
    }

    @Suppress("UNCHECKED_CAST")
    override fun getMvpView(): V = this as V

    override fun showContent() {
        // 空实现
    }

    override fun showLoading() {
        // 空实现
    }

    override fun showError(throwable: Throwable) {
        throwable.printStackTrace()
    }
}