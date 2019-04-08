package cn.linkfeeling.link_websocket;

import android.support.annotation.NonNull;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import okio.ByteString;

public abstract class WebSocketSubscriber implements Observer<WebSocketInfo> {
    @Override
    public void onSubscribe(Disposable d) {
    }

    @Override
    public void onNext(@NonNull WebSocketInfo webSocketInfo) {
        if (webSocketInfo == null) {
            return;
        }
        if (webSocketInfo.isOnOpen()) {
            onOpen(webSocketInfo.getWebSocket());
        }  else if (webSocketInfo.getString() != null) {
            onMessage(webSocketInfo.getString());
        } else if (webSocketInfo.getByteString() != null) {
            onMessage(webSocketInfo.getByteString());
        } else if (webSocketInfo.isOnReconnect()) {
            onReconnect();
        }
    }

    /**
     * Callback when the WebSocket is opened
     *
     * @param webSocket
     */
    protected void onOpen(@NonNull WebSocket webSocket) {
    }

    protected void onMessage(@NonNull String text) {
    }

    protected void onMessage(@NonNull ByteString byteString) {
    }

    /**
     * Callback when the WebSocket is reconnecting
     */
    protected void onReconnect() {
    }

    protected void onClose() {

    }

    @Override
    public final void onComplete() {

    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
    }

}
