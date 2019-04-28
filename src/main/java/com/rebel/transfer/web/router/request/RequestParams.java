package com.rebel.transfer.web.router.request;

import java.util.List;
import java.util.Map;

public class RequestParams {
    private final Map<String, List<String>> parameters;

    RequestParams(Map<String, List<String>> parameters) {
        this.parameters = parameters;
    }

    public String getString(String name) {
        try {
            return parameters.get(name).get(0);
        } catch (Exception e) {
            throw new ValidationException("Mandatory param `%s` not found", name);
        }
    }

    public Long getLong(String name) {
        var value = getString(name);

        try {
           return Long.parseLong(value);
        } catch (Exception e) {
            throw new ValidationException("Mandatory param `%s` expected type `long` got value `%s`", name, value);
        }
    }
}
