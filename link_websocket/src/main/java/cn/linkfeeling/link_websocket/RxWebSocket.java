package cn.linkfeeling.link_websocket;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okio.ByteString;


public final class RxWebSocket {


    public static void setConfig(Config config) {
        RxWebSocketUtil instance = RxWebSocketUtil.getInstance();
        instance.setShowLog(config.showLog, config.logTag);
        instance.setClient(config.client);
        instance.setReconnectInterval(config.reconnectInterval, config.reconnectIntervalTimeUnit);
        if (config.sslSocketFactory != null && config.trustManager != null) {
            instance.setSSLSocketFactory(config.sslSocketFactory, config.trustManager);
        }
    }

    /**
     * default timeout: 30 days
     * <p>
     * 若忽略小米平板,请调用这个方法
     * </p>
     */
    public static Observable<WebSocketInfo> get(String url) {
        return RxWebSocketUtil.getInstance().getWebSocketInfo(url);
    }

    /**
     * @param url      ws://127.0.0.1:8080/websocket
     * @param timeout  The WebSocket will be reconnected after the specified time interval is not "onMessage",
     *                 <p>
     *                 在指定时间间隔后没有收到消息就会重连WebSocket,为了适配小米平板,因为小米平板断网后,不会发送错误通知
     * @param timeUnit unit
     * @return
     */
    public static Observable<WebSocketInfo> get(String url, long timeout, TimeUnit timeUnit) {
        return RxWebSocketUtil.getInstance().getWebSocketInfo(url, timeout, timeUnit);
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param msg
     */
    public static void send(String url, String msg) {
        RxWebSocketUtil.getInstance().send(url, msg);
    }

    /**
     * 如果url的WebSocket已经打开,可以直接调用这个发送消息.
     *
     * @param url
     * @param byteString
     */
    public static void send(String url, ByteString byteString) {
        RxWebSocketUtil.getInstance().send(url, byteString);
    }

    /**
     * 根据url关闭websocket长连接
     *
     * @author zhangyong
     * @time 2019/3/12 11:41
     */
    public void closeSocketByUrl(String url) {
        RxWebSocketUtil.getInstance().closeWebSocket(url);
    }
}
