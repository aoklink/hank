package com.link.feeling.mvp.delegate

import android.os.Bundle
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView
import org.jetbrains.annotations.Nullable

/**
 * Created on 2019/1/11  15:31
 * chenpan pan.chen@linkfeeling.cn
 */
interface ActivityMvpDelegate<V : MvpView,P : MvpPresenter<V>> {

    fun onCreate(@Nullable bundle: Bundle?)

    fun onDestroy()

    fun onPause()

    fun onResume()

    fun onStart()

    fun onStop()

    fun onRestart()

    fun onContentChanged()

    fun onSaveInstanceState(@Nullable outState: Bundle?)

    fun onPostCreate(@Nullable bundle: Bundle?)

}