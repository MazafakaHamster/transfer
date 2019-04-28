package com.rebel.transfer.web.router;

import java.util.List;
import java.util.Map;

public class Request {
    public final Map<String, List<String>> parameters;
    public final String content;

    Request(Map<String, List<String>> parameters, String content) {
        this.parameters = parameters;
        this.content = content;
    }
}
