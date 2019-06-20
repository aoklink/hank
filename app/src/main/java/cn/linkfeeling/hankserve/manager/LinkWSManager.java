package cn.linkfeeling.hankserve.manager;

import android.support.annotation.NonNull;
import android.util.Log;

import com.link.feeling.framework.executor.ThreadPoolManager;
import com.link.feeling.framework.utils.data.L;

import cn.linkfeeling.hankserve.BuildConfig;
import cn.linkfeeling.link_websocket.RxWebSocket;
import cn.linkfeeling.link_websocket.WebSocketSubscriber;
import io.reactivex.disposables.Disposable;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * @author create by zhangyong
 * @time 2019/6/20
 */
public class LinkWSManager {
    private final String IP = "116.62.124.200";
    private final String PORT = "3510";
    private final String URL = "ws://" + IP + ":" + PORT + "/hank/" + BuildConfig.GYM_NAME;

    public static LinkWSManager getInstance() {
        return LinkWSManagerHolder.linkWSManagerProcessor;
    }

    private static class LinkWSManagerHolder {
        private static final LinkWSManager linkWSManagerProcessor = new LinkWSManager();
    }


    public void connectLinkWsConnect(ILinkDataCallBack iLinkDataCallBack) {


        ThreadPoolManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                RxWebSocket.get(URL).subscribe(new WebSocketSubscriber() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        super.onSubscribe(d);
                    }

                    @Override
                    protected void onOpen(@NonNull WebSocket webSocket) {
                        super.onOpen(webSocket);
                        L.i("========link", "link-open");
                    }

                    @Override
                    protected void onMessage(@NonNull String text) {
                        super.onMessage(text);
                        L.i("========link", text);
                        iLinkDataCallBack.receive(text);
                    }

                    @Override
                    protected void onMessage(@NonNull ByteString byteString) {
                        super.onMessage(byteString);
                        L.i("========link", byteString.hex());
                    }

                    @Override
                    protected void onReconnect() {
                        super.onReconnect();
                        L.i("========link", "link_onReconnect");
                    }

                    @Override
                    protected void onClose() {
                        super.onClose();
                        L.i("========link", "link_onClose");
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        L.i("========link", "link_onError");
                    }
                });
            }
        });

    }


    public void connectWebSocket(ILinkDataCallBack callBack) {
        ThreadPoolManager.getInstance().execute(() -> {
            String url = "ws://47.111.183.148:8083/websocket/";
            String api_token = "projAdmin_fb84d0dbf481f46f8f760ab3092d9a64fe78f217";
            //  String api_token = "projAdmin_3eb3a71f555ff04d3088e4199987af58c3d1e029";
            String project_name = BuildConfig.PROJECT_NAME;
            StringBuilder builder = new StringBuilder(url);
            builder.append(project_name);
            builder.append("_0");
            builder.append("_2D");
            builder.append(api_token);
            builder.append("_type|coord");
            createWsConnect(builder.toString(), callBack);
        });
    }

    /**
     * 连接uwb基站webSocket
     *
     * @param url
     */
    private void createWsConnect(String url, ILinkDataCallBack callBack) {
        RxWebSocket.get(url).subscribe(new WebSocketSubscriber() {
            @Override
            public void onSubscribe(Disposable d) {
                super.onSubscribe(d);
            }

            @Override
            protected void onOpen(@NonNull WebSocket webSocket) {
                super.onOpen(webSocket);
                L.i("========", "open");
            }

            @Override
            protected void onMessage(@NonNull String text) {
                super.onMessage(text);
                callBack.receive(text);

            }

            @Override
            protected void onMessage(@NonNull ByteString byteString) {
                super.onMessage(byteString);
            }

            @Override
            protected void onReconnect() {
                super.onReconnect();
                L.i("========", "onReconnect");
            }


            @Override
            protected void onClose() {
                super.onClose();
                L.i("========", "onClose");
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                L.i("========", "onError");
            }
        });
    }


    public interface ILinkDataCallBack {
        void receive(String text);
    }
}
