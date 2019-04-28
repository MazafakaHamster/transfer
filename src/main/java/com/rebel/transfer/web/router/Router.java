package com.rebel.transfer.web.router;

import com.rebel.transfer.web.router.request.Request;
import com.rebel.transfer.web.router.request.ValidationException;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelHandler.Sharable
public class Router extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger logger = Logger.getLogger(Router.class.getName());
    private final List<Route> routes;

    Router(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        var decoder = new QueryStringDecoder(msg.uri());
        try {
            var response = routes
                .stream()
                .filter(r -> msg.method().equals(r.method) && decoder.path().equals(r.path))
                .findFirst()
                .map(r -> r.handle(new Request(decoder.parameters(), msg.content().toString(CharsetUtil.UTF_8))))
                .orElseGet(() -> error(HttpResponseStatus.NOT_FOUND));
            ctx.write(response);
        } catch (ValidationException e) {
            logger.log(Level.INFO, "Validation exception: " + e.getMessage());
            ctx.write(error(HttpResponseStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Router error", e);
            ctx.write(error(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse error(HttpResponseStatus status) {
        return error(status, status.reasonPhrase());
    }

    private FullHttpResponse error(HttpResponseStatus status, String message) {
        var content = Unpooled.copiedBuffer(message, CharsetUtil.UTF_8);
        var response = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            status,
            content
        );
        response.headers()
            .set(HttpHeaderNames.CONTENT_TYPE, "text/plain")
            .set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        return response;
    }
}