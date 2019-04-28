package com.rebel.transfer.web;

import com.rebel.transfer.web.router.RouteBuilder;
import com.rebel.transfer.web.router.Router;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

public class TransferWebServer extends NettyWebServerBase {

    private Logger logger = Logger.getLogger(TransferWebServer.class.getName());

    public TransferWebServer(int port) {
        super(port);
    }

    @Override
    protected Router router() {
        return new RouteBuilder()
            .get("/balance", request -> {

                var key1 = request.parameters.get("key1").get(0);
                var key2 = request.parameters.get("key2").get(0);


                ByteBuf content = Unpooled.copiedBuffer("Result: " + (Integer.parseInt(key1) + Integer.parseInt(key2)), CharsetUtil.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    content
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                return response;
            })
            .post("/add", request -> {
                ByteBuf content = Unpooled.copiedBuffer("World", CharsetUtil.UTF_8);
                FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    content
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
                return response;
            })
            .build();
    }
}