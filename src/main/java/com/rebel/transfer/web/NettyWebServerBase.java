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
import lombok.SneakyThrows;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public abstract class NettyWebServerBase {

    private final int port;
    private Channel channel;

    NettyWebServerBase(int port) {
        this.port = port;
    }

    protected abstract Router router(ExecutorService executor);

    @SneakyThrows
    public void run(ExecutorService executor, CompletableFuture<Void> startListener) {
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
                            .addLast(router(executor));
                    }
                })
                .channel(NioServerSocketChannel.class);

            channel = bootstrap.bind(port).sync().channel();

            startListener.complete(null);
            channel.closeFuture().sync();
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public void shutdown() {
        channel.close();
    }
}