package com.rebel.transfer.web.router;

import com.rebel.transfer.web.router.request.Request;
import io.netty.handler.codec.http.HttpMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class RouteBuilder {

    private final List<Route>     routes;
    private final ExecutorService executor;

    public RouteBuilder(ExecutorService executor) {
        this.routes = new LinkedList<>();
        this.executor = executor;
    }

    public RouteBuilder get(String uri, Consumer<Request> handler) {
        return addRequest(HttpMethod.GET, uri, handler);
    }

    public RouteBuilder post(String uri, Consumer<Request> handler) {
        return addRequest(HttpMethod.POST, uri, handler);
    }

    private RouteBuilder addRequest(
        HttpMethod method,
        String uri,
        Consumer<Request> handler
    ) {
        routes.add(new Route(method, uri) {
            @Override
            public void handle(Request request) {
                handler.accept(request);
            }
        });
        return this;
    }

    public Router build() {
        return new Router(routes, executor);
    }
}
