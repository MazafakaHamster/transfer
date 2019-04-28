package com.rebel.transfer.web.router.request;

import java.util.List;
import java.util.Map;

public class Request {
    public final RequestParams params;
    public final String content;

    public Request(Map<String, List<String>> parameters, String content) {
        this.params = new RequestParams(parameters);
        this.content = content;
    }
}
