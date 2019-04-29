package com.rebel.transfer.web.router.request;

import com.rebel.transfer.web.router.response.Response;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;
import java.util.Map;

public class Request {
    public final  RequestParams         params;
    private final ChannelHandlerContext ctx;

    public Request(Map<String, List<String>> parameters, ChannelHandlerContext ctx) {
        this.params = new RequestParams(parameters);
        this.ctx = ctx;
    }

    public void end(Response response) {
        ctx.writeAndFlush(response.toNetty());
    }
}
