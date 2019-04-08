package com.link.feeling.framework.utils.rx;

import android.support.annotation.Keep;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * Created on 2019/1/14  17:03
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
@Keep
public final class Rx2Bus {

    private final Subject<Object> mPublishSubject;

    public static Rx2Bus getInstance() {
        return Holder.INSTANCE;
    }

    private Rx2Bus() {
        mPublishSubject = PublishSubject.create().toSerialized();
    }

    public void post(Object o) {
        mPublishSubject.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> clazz) {
        return mPublishSubject.ofType(clazz);
    }


    private static class Holder {
        private static final Rx2Bus INSTANCE = new Rx2Bus();
    }

}
