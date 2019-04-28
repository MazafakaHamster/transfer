package com.rebel.transfer.web.router;

import com.rebel.transfer.web.router.request.Request;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class RouteBuilder {

    private final List<Route> routes;

    public RouteBuilder() {
        this.routes = new LinkedList<>();
    }

    public RouteBuilder get(String uri, Function<Request, FullHttpResponse> handler) {
        return addRequest(HttpMethod.GET, uri, handler);
    }

    public RouteBuilder post(String uri, Function<Request, FullHttpResponse> handler) {
        return addRequest(HttpMethod.POST, uri, handler);
    }

    private RouteBuilder addRequest(
        HttpMethod method,
        String uri,
        Function<Request, FullHttpResponse> handler
    ) {
        routes.add(new Route(method, uri) {
            @Override
            public FullHttpResponse handle(Request request) {
                return handler.apply(request);
            }
        });
        return this;
    }

    public Router build() {
        return new Router(routes);
    }
}
