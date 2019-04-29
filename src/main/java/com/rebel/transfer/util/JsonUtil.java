package com.rebel.transfer.util;

import org.json.JSONObject;

import java.util.Map;

public class JsonUtil {

    public static JSONObject json(String key, Object value) {
        return new JSONObject(Map.of(key, value));
    }

    public static JSONObject json(String key1, Object value1, String key2, Object value2) {
        return new JSONObject(Map.of(key1, value1, key2, value2));
    }
}
