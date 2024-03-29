package cn.linkfeeling.link_socketserve;


import cn.linkfeeling.link_socketserve.handler.ChildChannelHandler;
import cn.linkfeeling.link_socketserve.interfaces.SocketCallBack;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {
    private static final int PORT = 9999;

    private NettyServer() {
    }

    public static NettyServer getInstance() {
        return NettyServerHolder.sNettyServerHolder;
    }

    private static class NettyServerHolder {
        private static final NettyServer sNettyServerHolder = new NettyServer();
    }


    public void bind(SocketCallBack socketCallBack) {
        //1、配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup(8);
        //2、创建ServerBootstrap
        ServerBootstrap sb = new ServerBootstrap();
        //3、
        sb.group(bossGroup, workGroup).
                channel(NioServerSocketChannel.class).
                option(ChannelOption.SO_BACKLOG, 1024).
                option(ChannelOption.TCP_NODELAY, true).
                option(ChannelOption.SO_KEEPALIVE, true).
                option(ChannelOption.SO_TIMEOUT, 60 * 1000).
                childHandler(new ChildChannelHandler(socketCallBack));

        try {
            //4、绑定端口，同步等待成功
            ChannelFuture cf = sb.bind(PORT).sync();
            //5、等待服务端监听端口关闭
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }


}