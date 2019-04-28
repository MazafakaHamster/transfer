package com.rebel.transfer.web.router;

import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

public abstract class Route {

    final HttpMethod method;
    final String     path;

    protected Route(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public abstract FullHttpResponse handle(Request request);

    @Override
    public String toString() {
        return "Route{" +
            "method=" + method +
            ", path='" + path + '\'' +
            '}';
    }
}