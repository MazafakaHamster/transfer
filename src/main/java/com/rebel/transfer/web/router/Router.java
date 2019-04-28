package com.rebel.transfer.web.router;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;

@ChannelHandler.Sharable
public class Router extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final List<Route> routes;

    public Router(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        var decoder = new QueryStringDecoder(msg.uri());
        var response = routes
            .stream()
            .filter(r -> msg.method().equals(r.method) && decoder.path().equals(r.path))
            .findFirst()
            .map(r -> r.handle(new Request(decoder.parameters(), msg.content().toString(CharsetUtil.UTF_8))))
            .orElseGet(this::notFound);
        ctx.write(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse notFound() {
        var content = Unpooled.copiedBuffer("Not Found 404", CharsetUtil.UTF_8);
        var response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.NOT_FOUND,
            content
        );
        response.headers()
            .set(HttpHeaderNames.CONTENT_TYPE, "text/plain")
            .set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        return response;
    }
}