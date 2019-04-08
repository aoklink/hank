package com.link.feeling.framework.base;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.link.feeling.framework.utils.data.ToastUtils;
import com.link.feeling.mvp.MvpFragment;

import org.jetbrains.annotations.NotNull;

import butterknife.ButterKnife;

/**
 * Created on 2019/1/18  09:45
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public abstract class FrameworkBaseFragment<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends MvpFragment<V, P> {

    private boolean mIsInit = false;
    @Nullable
    private Bundle mSavedInstanceState;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(), container, false);
    }

    @Override
    public void onViewCreated(@NotNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        mSavedInstanceState = savedInstanceState;
        if (lazyInit()) {
            // 需要懒加载
            if (viewPagerMode() && getUserVisibleHint() && isVisible()) {
                callInit(savedInstanceState);
            } else if (!viewPagerMode() && isVisible()) {
                callInit(savedInstanceState);
            }
        } else {
            callInit(savedInstanceState);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!viewPagerMode() && isVisible()) {
            callInit(mSavedInstanceState);
        }
    }

    public boolean isInit() {
        return mIsInit;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        ToastUtils.cancel();
        super.onDestroy();
    }

    // FragmentAdapter 会调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (viewPagerMode() && isVisibleToUser && isVisible()) {
            callInit(mSavedInstanceState);
        }
        if (viewPagerMode() && mIsInit){
            onHiddenChanged(!isVisibleToUser);
        }
    }

    /**
     * 显示 Toast
     *
     * @param text text
     */
    @UiThread
    public final void showToast(@NonNull final String text) {
        ToastUtils.showToast(text);
    }

    /**
     * 显示 Toast
     *
     * @param text text
     */
    @UiThread
    public void showToast(@StringRes final int text) {
        ToastUtils.showToast(text);
    }

    /**
     * 显示错误信息
     *
     * @param errorMsg 错误信息
     */
    public void showErrorStatus(String errorMsg) {
        if (!TextUtils.isEmpty(errorMsg)) {
            showToast(errorMsg);
        }
    }

    /**
     * 显示正常信息
     *
     * @param msg 信息
     */
    public void showNormalStatus(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            showToast(msg);
        }
    }

    /**
     * 显示加载
     *
     * @param enable 是否启动
     */
    public void showLoadingStatus(boolean enable) {
        if (getActivity() instanceof FrameworkBaseActivity) {
            ((FrameworkBaseActivity) getActivity()).showLoadingStatus(enable);
        }
    }

    /**
     * 获取布局
     *
     * @return layout res
     */
    protected abstract int getLayoutRes();

    /**
     * 初始化，在 Presenter 准备完毕
     *
     * @param savedInstanceState bundle
     */
    protected abstract void init(@Nullable Bundle savedInstanceState);


    /**
     * 在 ContentView onPreDraw 之前调用，在这里可以获取控件的测量尺寸
     * {@link View#getMeasuredWidth()} 和 {@link View#getMeasuredHeight()}
     */
    protected boolean onPreDraw() {
        return true;
    }

    /**
     * 是否需要懒加载
     *
     * @return 懒加载
     */
    protected boolean lazyInit() {
        return false;
    }

    /**
     * 是否为 viewpager 模式，会在 {@link #setUserVisibleHint(boolean)} 在进行加载
     *
     * @return viewPager 模式
     */
    protected boolean viewPagerMode() {
        return false;
    }

    /**
     * 延迟初始化
     */
    private void callInit(@Nullable Bundle savedInstanceState) {
        if (!mIsInit) {
            getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    getView().getViewTreeObserver().removeOnPreDrawListener(this);
                    return FrameworkBaseFragment.this.onPreDraw();
                }
            });
            init(savedInstanceState);
            if (getPresenter() != null) {
                getPresenter().initSuccess();
            }
            mIsInit = true;

        }
    }


}