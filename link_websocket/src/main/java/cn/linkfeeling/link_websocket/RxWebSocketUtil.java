package cn.linkfeeling.link_websocket;

import android.os.SystemClock;
import android.support.v4.util.ArrayMap;
import android.util.Log;


import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Cancellable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.internal.operators.completable.CompletableDisposeOn;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class RxWebSocketUtil {
    private static volatile RxWebSocketUtil instance;

    private OkHttpClient client;

    private ConcurrentHashMap<String, Observable<WebSocketInfo>> observableMap;
    private ConcurrentHashMap<String, WebSocket> webSocketMap;
    private boolean showLog;
    private String logTag = "RxWebSocket";
    private long interval = 1;
    private TimeUnit reconnectIntervalTimeUnit = TimeUnit.SECONDS;

    private RxWebSocketUtil() {
        observableMap = new ConcurrentHashMap<>();
        webSocketMap = new ConcurrentHashMap<>();
        client = new OkHttpClient();
    }

    /**
     * please use {@link RxWebSocket} to instead of it
     *
     * @return
     */
    public static RxWebSocketUtil getInstance() {
        if (instance == null) {
            synchronized (RxWebSocketUtil.class) {
                if (instance == null) {
                    instance = new RxWebSocketUtil();
                }
            }
        }
        return instance;
    }

    /**
     * set your client
     *
     * @param client
     */
    public void setClient(OkHttpClient client) {
        if (client == null) {
            throw new NullPointerException(" Are you stupid ? client == null");
        }
        this.client = client;
    }

    /**
     * wss support
     *
     * @param sslSocketFactory
     * @param trustManager
     */
    public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
        client = client.newBuilder().sslSocketFactory(sslSocketFactory, trustManager).build();
    }

    public void setShowLog(boolean showLog) {
        this.showLog = showLog;
    }

    public void setShowLog(boolean showLog, String logTag) {
        setShowLog(showLog);
        this.logTag = logTag;
    }

    public void setReconnectInterval(long interval, TimeUnit timeUnit) {
        this.interval = interval;
        this.reconnectIntervalTimeUnit = timeUnit;
    }

    /**
     * @param url      ws://127.0.0.1:8080/websocket
     * @param timeout  The WebSocket will be reconnected after the specified time interval is not "onMessage",
     *                 <p>
     *                 在指定时间间隔后没有收到消息就会重连WebSocket,为了适配小米平板,因为小米平板断网后,不会发送错误通知
     * @param timeUnit unit
     * @return
     */
    public Observable<WebSocketInfo> getWebSocketInfo(final String url, final long timeout, final TimeUnit timeUnit) {
        Observable<WebSocketInfo> observable = observableMap.get(url);
        if (observable == null) {
            observable = Observable.create(new WebSocketOnSubscribe(url))
                    //自动重连
                    .timeout(timeout, timeUnit)
                    .retry()
                    .doOnDispose(new Action() {
                        @Override
                        public void run() throws Exception {
                            observableMap.remove(url);
                            webSocketMap.remove(url);
                            if (showLog) {
                                Log.d(logTag, "unsubscribe");
                            }
                        }
                    })
                    .doOnNext(new Consumer<WebSocketInfo>() {
                        @Override
                        public void accept(WebSocketInfo webSocketInfo) throws Exception {
                            if (webSocketInfo.isOnOpen()) {
                                webSocketMap.put(url, webSocketInfo.getWebSocket());
                            }
                        }
                    })
                    .share()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            observableMap.put(url, observable);
        } else {
            WebSocket webSocket = webSocketMap.get(url);
            if (webSocket != null) {
                observable = observable.startWith(new WebSocketInfo(webSocket, true));
            }
        }
        return observable;
    }

    /**
     * default timeout: 30 days
     * <p>
     * 若忽略小米平板,请调用这个方法
     * </p>
     */
    public Observable<WebSocketInfo> getWebSocketInfo(String url) {
        return getWebSocketInfo(url, 1, TimeUnit.DAYS);
    }

    public Observable<String> getWebSocketString(String url) {
        return getWebSocketInfo(url)
                .map(new Function<WebSocketInfo, String>() {
                    @Override
                    public String apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getString();
                    }
                })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        return s != null;
                    }
                });
    }

    public Observable<ByteString> getWebSocketByteString(String url) {
        return getWebSocketInfo(url)
                .map(new Function<WebSocketInfo, ByteString>() {
                    @Override
                    public ByteString apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getByteString();
                    }
                })
                .filter(new Predicate<ByteString>() {
                    @Override
                    public boolean test(ByteString byteString) throws Exception {
                        return byteString != null;
                    }
                });
    }

    public Observable<WebSocket> getWebSocket(String url) {
        return getWebSocketInfo(url)
                .map(new Function<WebSocketInfo, WebSocket>() {
                    @Override
                    public WebSocket apply(WebSocketInfo webSocketInfo) throws Exception {
                        return webSocketInfo.getWebSocket();
                    }
                });
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param msg
     */
    public void send(String url, String msg) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(msg);
        } else {
            throw new IllegalStateException("The WebSocket not open");
        }
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param byteString
     */
    public void send(String url, ByteString byteString) {
        WebSocket webSocket = webSocketMap.get(url);
        if (webSocket != null) {
            webSocket.send(byteString);
        } else {
            throw new IllegalStateException("The WebSokcet not open");
        }
    }

    private Request getRequest(String url) {
        return new Request.Builder()
                .get()
                .url(url)
                .addHeader("gym_id","link_office")
                .build();
    }

    private final class WebSocketOnSubscribe implements ObservableOnSubscribe<WebSocketInfo> {
        private String url;

        private WebSocket webSocket;

        public WebSocketOnSubscribe(String url) {
            this.url = url;
        }

        @Override
        public void subscribe(ObservableEmitter<WebSocketInfo> e) throws Exception {
            if (webSocket != null) {
                //降低重连频率 设置时间间隔
                if (!"main".equals(Thread.currentThread().getName())) {
                    long ms = reconnectIntervalTimeUnit.toMillis(interval);
                    if (ms == 0) {
                        ms = 1000;
                    }
                    SystemClock.sleep(ms);
                    e.onNext(WebSocketInfo.createReconnect());
                }
            }
            initWebSocket(e);
        }

        /**
         * 初始化webSocket
         *
         * @author zhangyong
         * @time 2019/3/12 17:38
         */
        private void initWebSocket(final ObservableEmitter<? super WebSocketInfo> subscriber) {
            webSocket = client.newWebSocket(getRequest(url), new WebSocketListener() {
                @Override
                public void onOpen(final WebSocket webSocket, Response response) {
                    if (showLog) {
                        Log.d(logTag, url + " --> onOpen");
                    }
                    webSocketMap.put(url, webSocket);
                    if (!subscriber.isDisposed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, true));
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {

                    if (showLog) {
                        Log.d(logTag, url + " --> " + text);
                    }
                    if (!subscriber.isDisposed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, text));
                    }
                }

                @Override
                public void onMessage(WebSocket webSocket, ByteString bytes) {

                    if (showLog) {
                        Log.d(logTag, url + " --> " + bytes);
                    }
                    if (!subscriber.isDisposed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, bytes));
                    }
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    if (showLog) {
                        Log.e(logTag, t.toString() + webSocket.request().url().uri().getPath());
                    }
                    if (!subscriber.isDisposed()) {
                        subscriber.onError(t);
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    webSocket.close(1000, null);
                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    if (showLog) {
                        Log.d(logTag, url + " --> onClosed:code = " + code + ", reason = " + reason);
                    }
                    if (!subscriber.isDisposed()) {
                        subscriber.onNext(new WebSocketInfo(webSocket, false));
                        subscriber.onError(new Throwable());
                    }
                    //if you want to continue open connection you can do  subscriber.onError(t);
                }
            });
        }


    }


    /**
     * 关闭socket长连接&&取消订阅
     *
     * @author zhangyong
     * @time 2019/3/12 11:43
     */
    public void closeWebSocket(String url) {
        if (webSocketMap != null && webSocketMap.get(url) != null) {
            WebSocket webSocket = webSocketMap.get(url);
            webSocket.close(1000, null);

        }
        if (observableMap != null && observableMap.get(url) != null) {
            Observable<WebSocketInfo> observable = observableMap.get(url);
            observable.subscribe().dispose();
        }
    }
}
