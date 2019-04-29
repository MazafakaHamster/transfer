package com.rebel.transfer.web.router;

import com.rebel.transfer.web.router.request.Request;
import com.rebel.transfer.web.router.response.Response;
import io.netty.handler.codec.http.HttpMethod;

public abstract class Route {

    final HttpMethod method;
    final String     path;

    Route(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public abstract Response handle(Request request);

    @Override
    public String toString() {
        return "Route{" +
            "method=" + method +
            ", path='" + path + '\'' +
            '}';
    }
}