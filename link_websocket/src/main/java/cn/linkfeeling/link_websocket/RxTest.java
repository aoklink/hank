package cn.linkfeeling.link_websocket;

import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author create by zhangyong
 * @time 2019/3/12
 */
public class RxTest {

    public static void main(String args[]){
        Observable<Object> objectObservable = Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> e) throws Exception {
                e.onNext(11);

            }
        }).doOnDispose(new Action() {
            @Override
            public void run() throws Exception {
                System.out.print("我被取消订阅了");


            }
        });

        objectObservable.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                System.out.print("我被订阅了");

            }
        });

        objectObservable.subscribe().dispose();




//        final FlowableEmitter<Object>[] ex = new FlowableEmitter[0];
//
//        Flowable.create(new FlowableOnSubscribe<Object>() {
//            @Override
//            public void subscribe(final FlowableEmitter<Object> e) throws Exception {
//
//
//
//                e.onNext(new IllegalAccessError());
//                e.onNext(new IllegalAccessError());
//                e.onNext(new IllegalAccessError());
//                e.onNext(new IllegalAccessError());
//
//            //    e.onError(new Throwable());
//
//            }
//        },BackpressureStrategy.BUFFER)
//                .retry()
//                .subscribe(new Subscriber<Object>() {
//            @Override
//            public void onSubscribe(Subscription s) {
//
//                s.request(1);
//                System.out.print("执行了");
//
//
//
//
//
//
//            }
//
//            @Override
//            public void onNext(Object o) {
//
//                System.out.print("执行了"+o);
//
//
//
//
//            }
//
//            @Override
//            public void onError(Throwable t) {
//
//                Log.i("www", "wwwwwwww");
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
    }
}
