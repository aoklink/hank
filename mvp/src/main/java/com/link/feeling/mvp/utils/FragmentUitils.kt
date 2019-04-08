package com.link.feeling.mvp.utils

import android.content.Context
import android.support.v4.app.Fragment
import java.util.*

/**
 * Created on 2019/1/11  16:47
 * chenpan pan.chen@linkfeeling.cn
 */
@Suppress("UNUSED")
object FragmentUtils {

    /**
     * 反射调用 Fragment.isInBackStack()
     */
    fun Fragment.isInBackStack(): Boolean {
        try {
            Fragment::class.java.getDeclaredMethod("isInBackStack").let {
                it.isAccessible = true
                return it.invoke(this) as Boolean
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    // 生成 View ID
    fun Context.toViewId(): String = UUID.randomUUID().toString()

}