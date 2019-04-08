package com.link.feeling.framework.component.rx;

import com.link.feeling.framework.base.BasePresenter;

import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;

/**
 * Created on 2019/1/17  11:52
 * chenpan pan.chen@linkfeeling.cn
 */
public class BaseCompletableObserver extends BasicObserver implements CompletableObserver {

    public BaseCompletableObserver(BasePresenter presenter) {
        super(presenter);
    }

    @Override
    public void onSubscribe(Disposable d) {
        super.onSubscribe(d);
    }

    @Override
    public void onError(Throwable e) {
        super.onError(e);
    }

    @Override
    public void onComplete() {
        super.onComplete();
    }
}
