package com.link.feeling.framework.component.rx;

import android.support.annotation.CallSuper;

import com.link.feeling.framework.base.BasePresenter;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2019/1/17  13:33
 * chenpan pan.chen@linkfeeling.cn
 */
public abstract class BaseSingleObserver<T> extends BasicObserver implements SingleObserver<T> {

    protected BaseSingleObserver(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    @CallSuper
    public void onSuccess(T t) {
        onComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }
}