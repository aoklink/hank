package com.link.feeling.framework.base;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.bigkoo.svprogresshud.SVProgressHUD;
import com.jude.swipbackhelper.SwipeBackHelper;
import com.link.feeling.framework.R;
import com.link.feeling.framework.utils.data.DisplayUtils;
import com.link.feeling.framework.utils.data.ToastUtils;
import com.link.feeling.framework.utils.ui.ActivityUtils;
import com.link.feeling.framework.utils.ui.KeyboardUtils;
import com.link.feeling.framework.utils.ui.ViewUtils;
import com.link.feeling.mvp.MvpActivity;

import butterknife.ButterKnife;

/**
 * Created on 2019/1/14  16:35
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public abstract class FrameworkBaseActivity<V extends BaseMvpView, P extends BaseMvpPresenter<V>> extends MvpActivity<V, P> {

    private boolean mAutoCancelToast = false;
    private boolean mIsInit = false;
    public SVProgressHUD mProgressHUD;
    private final static Handler MAIN_HANDLER = new MainHandler();
    // 延迟处理事件
    private static final int WHAT_DELAY = 111;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        // 側滑返回
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)//获取当前页面
                .setSwipeBackEnable(true)//设置是否可滑动
                .setSwipeEdgePercent(0.08f)//可滑动的范围。百分比。0.2表示为左边20%的屏幕
                .setSwipeSensitivity(0.5f)//敏感程度
                .setScrimColor(Color.TRANSPARENT)// 底层颜色
                .setSwipeRelateEnable(true)//是否与下一级activity联动(微信效果)。默认关
                .setSwipeRelateOffset(200);//activity联动时的偏移量。默认500px。
        setContentView(getLayoutRes());
        // 透明状态栏
        //   DisplayUtils.translucentStatusBar(this);
        ButterKnife.bind(this);
        if (getPresenter() == null) {
            finish();
            return;
        }
        final View contentView = ActivityUtils.getActivityContentView(this);
        if (contentView != null) {
            contentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    contentView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return FrameworkBaseActivity.this.onPreDraw();
                }
            });
        }
        mIsInit = false;
        if (mProgressHUD == null) {
            mProgressHUD = new SVProgressHUD(this);
        }
        init(savedInstanceState);
        mIsInit = true;
        getPresenter().initSuccess();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 关闭软键盘
        KeyboardUtils.hideSoftInput(this);
        if (mProgressHUD != null && mProgressHUD.isShowing()) {
            mProgressHUD.dismiss();
            mProgressHUD.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        SwipeBackHelper.onDestroy(this);
        if (mAutoCancelToast) {
            ToastUtils.cancel();
        }
        MAIN_HANDLER.removeMessages(WHAT_DELAY);
        KeyboardUtils.fixSoftInputLeaks(this);
        super.onDestroy();
    }

    public boolean isInit() {
        return mIsInit;
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
     * 获取上下文
     *
     * @return 上下文
     */
    public final Context getContext() {
        return this;
    }

    /**
     * 显示 Toast
     *
     * @param text text
     */
    @UiThread
    protected void showToast(@StringRes final int text) {
        ToastUtils.showToast(text);
    }

    /**
     * 显示错误信息
     *
     * @param errorMsg 错误信息
     */
    public void showErrorStatus(String errorMsg) {
        mProgressHUD.showErrorWithStatus(errorMsg);
    }

    /**
     * 显示正常信息
     *
     * @param msg 信息
     */
    public void showNormalStatus(String msg) {
        mProgressHUD.showInfoWithStatus(msg);
    }

    /**
     * 显示加载
     *
     * @param enable 是否启动
     */
    public void showLoadingStatus(boolean enable) {
        if (enable) {
            mProgressHUD.showWithStatus(ViewUtils.getString(R.string.loading), SVProgressHUD.SVProgressHUDMaskType.Black);
        } else {
            mProgressHUD.dismiss();
        }
    }

    /**
     * 是否在 Activity 关闭时取消 Toast
     *
     * @param autoCancelToast 自动取消 Toast
     */
    protected final void setAutoCancelToast(boolean autoCancelToast) {
        mAutoCancelToast = autoCancelToast;
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
     * 延迟执行事件
     *
     * @param runnable    执行
     * @param delayMillis 延时
     */
    protected void postDelay(Runnable runnable, long delayMillis) {
        if (runnable == null) {
            return;
        }
        MAIN_HANDLER.sendMessageDelayed(
                MAIN_HANDLER.obtainMessage(WHAT_DELAY, runnable),
                delayMillis
        );
    }

    protected boolean isCountFragment() {
        return false;
    }

    private static class MainHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_DELAY && msg.obj instanceof Runnable) {
                ((Runnable) msg.obj).run();
            }
        }
    }

}

