package com.rebel.transfer.web.router;

import com.rebel.transfer.web.router.exceptions.NotFoundException;
import com.rebel.transfer.web.router.exceptions.BadRequestException;
import com.rebel.transfer.web.router.request.Request;
import com.rebel.transfer.web.router.response.Response;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.rebel.transfer.util.JsonUtil.json;

@ChannelHandler.Sharable
public class Router extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final Logger          logger = Logger.getLogger(Router.class.getName());
    private final List<Route>     routes;
    private final ExecutorService executor;

    Router(List<Route> routes, ExecutorService executor) {
        this.routes = routes;
        this.executor = executor;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        logger.log(Level.INFO, () -> "Got request: " + msg.uri());

        var decoder = new QueryStringDecoder(msg.uri());

        var request = new Request(decoder.parameters(), ctx);
        var route = routes
            .stream()
            .filter(r -> msg.method().equals(r.method) && decoder.path().equals(r.path))
            .findFirst()
            .orElseThrow(NotFoundException::new);

        handle(route, request, ctx);
    }

    private void handle(Route route, Request request, ChannelHandlerContext ctx) {
        executor.submit(() -> {
                try {
                    route.handle(request);
                } catch (Exception e) {
                    handleException(ctx, e);
                }
            }
        );
    }

    private void handleException(ChannelHandlerContext ctx, Exception cause) {
        if (cause instanceof BadRequestException) {
            logger.log(Level.WARNING, () -> "Bad Request: " + cause.getMessage());
            ctx.writeAndFlush(
                Response.custom(
                    HttpResponseStatus.BAD_REQUEST,
                    json("errorMessage", cause.getMessage())
                ).toNetty()
            );
        } else {
            logger.log(Level.SEVERE, "Router exception", cause);
            ctx.writeAndFlush(
                Response.custom(
                    HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    json("errorMessage", "Unknown error, contact administrator")
                ).toNetty());
        }
    }
}