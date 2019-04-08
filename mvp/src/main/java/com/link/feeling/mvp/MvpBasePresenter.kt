package com.link.feeling.mvp

import android.os.Handler
import android.os.Looper
import android.support.annotation.CallSuper
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Created on 2019/1/14  14:54
 * chenpan pan.chen@linkfeeling.cn
 */
@Suppress("UNUSED")
open class MvpBasePresenter<V : MvpView> : MvpPresenter<V> {

    var viewRef: WeakReference<V>? = null

    var presenterDestroyed: Boolean = false

    val viewActionQueue = ConcurrentLinkedQueue<ViewAction<V>>()


    /**
     * 不推荐继承类去管理 View，需要调用 View 的方法使用 onceViewAttached 或 ifViewAttached
     */
    @CallSuper
    override fun attachView(view: V) {
        presenterDestroyed = false
        viewRef = WeakReference(view)
        runQueuedActions()
    }

    @CallSuper
    override fun detachView() {
        viewRef?.clear()
    }

    @CallSuper
    override fun destroy() {
        viewActionQueue.clear()
        presenterDestroyed = true
    }


    /**
     * 开始执行 View Action 队列
     */
    protected fun onceViewAttached(action: (view: V) -> Unit) {
        viewActionQueue.add(object : ViewAction<V> {
            override fun run(view: V) {
                action.invoke(view)
            }
        })
        runQueuedActions()
    }

    /**
     * 开始执行 View Action 队列
     */
    protected fun onceViewAttached(action: ViewAction<V>) {
        viewActionQueue.add(action)
        runQueuedActions()
    }

    /**
     * 只有 view attach 才执行
     * @param exceptionIfViewNotAttached 如果 view not attach 是否抛出异常 IllegalStateException
     * @throws IllegalStateException
     */
    protected fun ifViewAttached(action: (view: V) -> Unit, exceptionIfViewNotAttached: Boolean = false) {
        ifViewAttached(object : ViewAction<V> {
            override fun run(view: V) {
                action.invoke(view)
            }

        }, exceptionIfViewNotAttached)
    }

    /**
     * 是否可以执行
     */
    protected open fun canExecute() = true

    /**
     * 开始执行队列中任务
     */
    protected fun runQueuedActions() {
        if (!canExecute()) {
            return
        }
        viewRef?.get()?.let {
            while (viewActionQueue.isNotEmpty()) {
                ifViewAttached(viewActionQueue.poll())
            }
        }
    }

    /**
     * 只有 view attach 才执行
     * @param exceptionIfViewNotAttached 如果 view not attach 是否抛出异常 IllegalStateException
     * @throws IllegalStateException
     */
    private fun ifViewAttached(action: ViewAction<V>, exceptionIfViewNotAttached: Boolean = false) {
        if (exceptionIfViewNotAttached) {
            val finalViewRef = viewRef
            if (finalViewRef == null || finalViewRef.get() == null) {
                throw IllegalStateException("View Not Attach")
            }
        }
        if (!canExecute()) {
            return
        }
        viewRef?.get()?.let {
            action.run(it)
        }
    }



    interface ViewAction<V> {

        fun run(view: V)

    }

    private class MainThreadHandler : Handler(Looper.getMainLooper())

}