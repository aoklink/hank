package com.link.feeling.mvp.delegate

import android.support.annotation.NonNull
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView

/**
 * Created on 2019/1/11  16:08
 * chenpan pan.chen@linkfeeling.cn
 */
interface MvpDelegateCallback <V : MvpView, P : MvpPresenter<V>> {


    @NonNull
    fun createPresenter(): P

    @NonNull
    fun getPresenter(): P

    fun setPresenter(@NonNull presenter: P)

    fun getMvpView(): V
}