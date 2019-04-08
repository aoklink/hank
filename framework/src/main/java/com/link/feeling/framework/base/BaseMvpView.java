package com.link.feeling.framework.base;

import android.content.Context;

import com.link.feeling.mvp.common.MvpView;

/**
 * Created on 2019/1/14  16:17
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public interface BaseMvpView extends MvpView {

    /**
     * 显示 toast
     *
     * @param message 内容
     */
    void showToast(String message);

    /**
     * 获取上下文
     *
     * @return 上下文
     */
    Context getContext();

    /**
     * 是否已经初始化
     *
     * @return 是否初始化
     */
    boolean isInit();

    /**
     * 显示错误信息
     *
     * @param errorMsg 错误信息
     */
    void showErrorStatus(String errorMsg);

    /**
     * 显示正常信息
     *
     * @param msg 信息
     */
    void showNormalStatus(String msg);

    /**
     * 显示加载
     * @param enable 是否启动
     */
    void showLoadingStatus(boolean enable);

}
