package com.rebel.transfer.web;

import com.rebel.transfer.web.router.Router;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public abstract class NettyWebServerBase {

    private final int port;

    NettyWebServerBase(int port) {
        this.port = port;
    }

    protected abstract Router router();

    public void run() throws Exception {
        var eventLoopGroup = new NioEventLoopGroup();
        try {

            var bootstrap = new ServerBootstrap()
                .group(eventLoopGroup)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline()
                            .addLast(new HttpServerCodec())
                            .addLast(new HttpObjectAggregator(512 * 1024))
                            .addLast(router());
                    }
                })
                .channel(NioServerSocketChannel.class);

            var ch = bootstrap.bind(port).sync().channel();
            ch.closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}