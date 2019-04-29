package com.rebel.transfer.web.router;

import com.rebel.transfer.web.router.request.Request;
import com.rebel.transfer.web.router.request.ValidationException;
import com.rebel.transfer.web.router.response.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@ChannelHandler.Sharable
public class Router extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger      logger = Logger.getLogger(Router.class.getName());
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
                .orElseGet(() -> Response.custom(HttpResponseStatus.NOT_FOUND));
            ctx.write(convertResponse(response));
        } catch (ValidationException e) {
            logger.log(Level.INFO, "Validation exception: " + e.getMessage());
            ctx.write(Response.custom(HttpResponseStatus.BAD_REQUEST, e.getMessage()));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Router custom", e);
            ctx.write(Response.custom(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private FullHttpResponse convertResponse(Response response) {
        return Optional
            .ofNullable(response.message())
            .map(m -> new JSONObject().put("message", m).toString())
            .map(m -> Unpooled.copiedBuffer(m, CharsetUtil.UTF_8))
            .map(c -> {
                var r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.httpResponseStatus(), c);
                r.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, "text/json")
                    .set(HttpHeaderNames.CONTENT_LENGTH, c.readableBytes());
                return r;
            })
            .orElseGet(() -> new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, response.httpResponseStatus()));
    }
}