package com.link.feeling.mvp

import android.content.Context
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.View
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import com.link.feeling.mvp.delegate.FragmentMvpDelegate
import com.link.feeling.mvp.delegate.MvpDelegateCallback
import com.link.feeling.mvp.delegate.impl.FragmentMvpDelegateImpl
import kotlin.LazyThreadSafetyMode.NONE
/**
 * Created on 2019/1/14  13:57
 * chenpan pan.chen@linkfeeling.cn
 */
abstract class MvpFragment<V : MvpView, P : MvpPresenter<V>> : Fragment(), MvpDelegateCallback<V, P>, MvpView {

    private var mvpPresenter: P? = null

    private val fragmentMvpDelegate: FragmentMvpDelegate<V, P> by lazy(NONE) {
        FragmentMvpDelegateImpl(this, this, true, true)
    }

    @CallSuper
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentMvpDelegate.onActivityCreated(savedInstanceState)
    }

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentMvpDelegate.onAttach(context)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentMvpDelegate.onCreate(savedInstanceState)
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentMvpDelegate.onViewCreated(view, savedInstanceState)
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        fragmentMvpDelegate.onSaveInstanceState(outState)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        fragmentMvpDelegate.onStart()
    }

    @CallSuper
    override fun onResume() {
        super.onResume()
        fragmentMvpDelegate.onResume()
    }

    @CallSuper
    override fun onPause() {
        super.onPause()
        fragmentMvpDelegate.onPause()
    }

    @CallSuper
    override fun onStop() {
        super.onStop()
        fragmentMvpDelegate.onStop()
    }

    @CallSuper
    override fun onDestroyView() {
        super.onDestroyView()
        fragmentMvpDelegate.onDestroyView()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        fragmentMvpDelegate.onDestroy()
    }

    @CallSuper
    override fun onDetach() {
        super.onDetach()
        fragmentMvpDelegate.onDetach()
    }

    override fun getPresenter(): P = checkNotNull(mvpPresenter) {
        "Presenter 为空，请先调用 setPresenter"
    }

    @CallSuper
    override fun setPresenter(presenter: P) {
        mvpPresenter = presenter
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