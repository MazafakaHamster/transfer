package com.rebel.transfer.web.router.response;

import io.netty.handler.codec.http.HttpResponseStatus;

public class Response {
    private HttpResponseStatus httpResponseStatus;
    private String             message;

    private Response(HttpResponseStatus httpResponseStatus, String message) {
        this.httpResponseStatus = httpResponseStatus;
        this.message = message;
    }

    public HttpResponseStatus httpResponseStatus() {
        return httpResponseStatus;
    }

    public String message() {
        return message;
    }

    public static Response ok() {
        return ok(null);
    }

    public static Response ok(String message) {
        return new Response(HttpResponseStatus.OK, message);
    }

    public static Response custom(HttpResponseStatus status) {
        return custom(status, null);
    }

    public static Response custom(HttpResponseStatus status, String message) {
        return new Response(status, message);
    }
}
