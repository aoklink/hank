package com.link.feeling.framework.component.rx;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;

import com.link.feeling.framework.base.BasePresenter;
import com.link.feeling.framework.component.net.config.HttpErrorHandler;
import com.link.feeling.framework.component.net.exception.NetException;
import com.link.feeling.framework.event.LoginEvent;
import com.link.feeling.framework.event.UserRegisterEvent;
import com.link.feeling.framework.utils.rx.Rx2Bus;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.disposables.Disposable;

/**
 * Created on 2019/1/17  11:54
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
abstract class BasicObserver {

    private static final AtomicBoolean sLoadingStatus = new AtomicBoolean(false);
    protected final MainHandler mMainHandler = new MainHandler(Looper.getMainLooper());
    private final BasePresenter mPresenter;
    // loading 显示延时时间
    private final static long LOADING_DELAY_MILLIS = 600;
    // 是否需要显示 loading

    private static final int WHAT_LOADING = 123;

    BasicObserver(BasePresenter presenter) {
        mPresenter = presenter;
    }

    public void onSubscribe(Disposable d) {
        mPresenter.addDisposable(d);
        mMainHandler.removeMessages(WHAT_LOADING);
        if (needLoading()) {
            sLoadingStatus.set(true);
            mMainHandler.sendMessageDelayed(mMainHandler.obtainMessage(WHAT_LOADING, mPresenter), LOADING_DELAY_MILLIS);
        }
    }

    public void onError(final Throwable e) {
        hideLoading();
        e.printStackTrace();
        if (e instanceof NetException) {
            // 业务异常
            NetException apiException = (NetException) e;
            try {
                HttpErrorHandler.handleException(apiException, new HttpErrorHandler.ErrorCallbackAdapter() {
                    @Override
                    public void needRegister(@NonNull String message, @NonNull String code) {
                        super.needRegister(message, code);
                        mPresenter.showToast(message);
                        userRegister();
                    }

                    @Override
                    public void needLogin(@NonNull String message, @NonNull String code) {
                        super.needLogin(message, code);
                        mPresenter.showToast(message);
                        login();
                    }

                    @Override
                    public void needShowStrongError(@NonNull String message, @NonNull String code) {
                        super.needShowStrongError(message, code);
                        if (mPresenter.getContext() instanceof Activity) {
//                            AlertDialogUtils.showMsgDialog(mPresenter.getContext(), message);
                        }
                    }

                    @Override
                    public void otherError(@NonNull NetException exception, String code, @NonNull String message) {
                        super.otherError(exception, code, message);
                        if (handleOtherError(exception, code, message)) {
                            return;
                        }
                     //   mPresenter.showErrorStatus(message);
                    }
                });
            } catch (Exception e1) {
                e1.printStackTrace();
              //  mPresenter.showToast(DisplayUtils.getString(R.string.framework_error_get));

            }
        } else {
          //  mPresenter.showToast(DisplayUtils.getString(R.string.framework_error_get));
        }
    }


    public void onComplete() {
        hideLoading();
    }

    protected boolean needLoading() {
        return true;
    }

    protected boolean handleOtherError(NetException exception, String code, String message) {
        return false;
    }

    protected void userRegister() {
        mMainHandler.postDelayed(() -> Rx2Bus.getInstance().post(new UserRegisterEvent()), 300);
    }

    private void login() {
        mMainHandler.postDelayed(() -> Rx2Bus.getInstance().post(new LoginEvent()), 200);
    }


    private void hideLoading() {
        sLoadingStatus.set(false);
        mPresenter.showLoadingStatus(false);
        mMainHandler.removeMessages(WHAT_LOADING);
    }

    protected static class MainHandler extends Handler {

        MainHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_LOADING && msg.obj instanceof BasePresenter && sLoadingStatus.get()) {
                BasePresenter presenter = (BasePresenter) msg.obj;
              //  presenter.showLoadingStatus(true);
            }
        }
    }

}

