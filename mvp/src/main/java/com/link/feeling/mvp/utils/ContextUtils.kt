package com.link.feeling.mvp.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.util.*

/**
 * Created on 2019/1/11  16:46
 * chenpan pan.chen@linkfeeling.cn
 */
object ContextUtils {

    /**
     * 通过 Context 获取 Activity 实例，不存在返回 null
     */
    fun Context.toActivity(): Activity? {
        if (this is Activity) {
            return this
        }
        var context: Context = this
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

    // 生成 View ID
    fun Context.toViewId(): String = UUID.randomUUID().toString()

}