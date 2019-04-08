package com.link.feeling.framework.component.rx;

import com.link.feeling.framework.base.BasePresenter;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2019/1/17  11:53
 * chenpan pan.chen@linkfeeling.cn
 */
public abstract class BaseObserver<T> extends BasicObserver implements Observer<T> {

    public BaseObserver(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onNext(T t) {
        onComplete();
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }
}
