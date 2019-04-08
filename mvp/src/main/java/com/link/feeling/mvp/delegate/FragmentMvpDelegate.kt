package com.link.feeling.mvp.delegate

import android.content.Context
import android.os.Bundle
import android.view.View
import com.link.feeling.mvp.common.MvpPresenter
import com.link.feeling.mvp.common.MvpView

/**
 * Created on 2019/1/11  16:07
 * chenpan pan.chen@linkfeeling.cn
 */
interface FragmentMvpDelegate <V : MvpView, P : MvpPresenter<V>> {

    fun onActivityCreated(bundle: Bundle?)

    fun onAttach(context: Context)

    fun onCreate(bundle: Bundle?)

    fun onViewCreated(view: View, bundle: Bundle?)

    fun onStart()

    fun onResume()

    fun onSaveInstanceState(bundle: Bundle?)

    fun onPause()

    fun onStop()

    fun onDestroyView()

    fun onDestroy()

    fun onDetach()

}