package com.rebel.transfer.web.router.response;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.json.JSONObject;

public class Response {
    private HttpResponseStatus httpResponseStatus;
    private JSONObject         content;

    private Response(HttpResponseStatus httpResponseStatus, JSONObject content) {
        this.httpResponseStatus = httpResponseStatus;
        this.content = content;
    }

    private HttpResponseStatus httpResponseStatus() {
        return httpResponseStatus;
    }

    private JSONObject content() {
        return content;
    }

    public static Response ok(JSONObject content) {
        return new Response(HttpResponseStatus.OK, content);
    }

    public static Response custom(HttpResponseStatus status, JSONObject content) {
        return new Response(status, content);
    }

    public FullHttpResponse toNetty() {
        var nettyContent = Unpooled.copiedBuffer(content().toString(), CharsetUtil.UTF_8);
        var r = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus(), nettyContent);
        r.headers()
            .set(HttpHeaderNames.CONTENT_TYPE, "text/json")
            .set(HttpHeaderNames.CONTENT_LENGTH, nettyContent.readableBytes());
        return r;
    }
}
