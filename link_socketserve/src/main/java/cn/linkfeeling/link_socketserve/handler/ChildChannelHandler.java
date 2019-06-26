package cn.linkfeeling.link_socketserve.handler;


import java.util.concurrent.TimeUnit;

import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;
import cn.linkfeeling.link_socketserve.netty.MyWebSocketHandler;
import cn.linkfeeling.link_socketserve.unpack.SmartCarDecoder;
import cn.linkfeeling.link_socketserve.unpack.SmartCarEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    private SocketCallBack socketCallBack;

    static final EventExecutorGroup group = new DefaultEventExecutorGroup(16);

    public ChildChannelHandler(SocketCallBack socketCallBack) {
        this.socketCallBack = socketCallBack;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

//
//        // 设置30秒没有读到数据，则触发一个READER_IDLE事件。
//// pipeline.addLast(new IdleStateHandler(30, 0, 0));
// HttpServerCodec：将请求和应答消息解码为HTTP消息
//        socketChannel.pipeline().addLast("http-codec", new HttpServerCodec());
//// HttpObjectAggregator：将HTTP消息的多个部分合成一条完整的HTTP消息
//        socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
//// ChunkedWriteHandler：向客户端发送HTML5文件
//        socketChannel.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
        //在管道中添加我们自己的接收数据实现方法
        ChannelPipeline pipeline = socketChannel.pipeline();

        pipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("logging", new LoggingHandler(LogLevel.INFO));
        // pipeline.addLast("http-codec", new HttpServerCodec());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
//        ByteBuf delimiter = Unpooled.copiedBuffer("||".getBytes());
//        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(2048, delimiter));

        //pipeline.addLast(new FixedLengthFrameDecoder(62));
        //  pipeline.addLast(new LineBasedFrameDecoder(2048));

        // 添加自定义协议的编解码工具
        pipeline.addLast(new SmartCarEncoder());
        pipeline.addLast(new SmartCarDecoder());
        pipeline.addLast("handler", new MyWebSocketHandler(socketCallBack));
        //   pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));


    }
}