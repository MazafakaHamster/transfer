package com.rebel.transfer;

import io.netty.handler.codec.http.QueryStringEncoder;
import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpClient implements Closeable {

    private final CloseableHttpClient client;
    private final String              host;
    private final int                 port;

    HttpClient(String host, int port) {
        this.client = HttpClientBuilder.create().build();
        this.host = host;
        this.port = port;
    }

    Response post(String uri) {
        return post(uri, new HashMap<>());
    }

    Response post(String uri, Map<String, String> params) {
        return performRequest(new HttpPost(buildQuery(uri, params)));
    }

    Response get(String uri) {
        return get(uri, new HashMap<>());
    }

    Response get(String uri, Map<String, String> params) {
        return performRequest(new HttpGet(buildQuery(uri, params)));
    }

    @SneakyThrows
    private Response performRequest(HttpRequestBase requestBase) {
        var response = client.execute(requestBase);
        var content = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
            .lines()
            .collect(Collectors.joining("\n"));
        var jsonContent = new JSONObject(content);
        return new Response(response.getStatusLine().getStatusCode(), jsonContent);
    }

    @SneakyThrows
    private String buildQuery(String uri, Map<String, String> params) {
        var url = new URL("http", host, port, uri);
        var encoder = new QueryStringEncoder(url.toString());
        params.forEach(encoder::addParam);
        return encoder.toString();
    }

    @Override
    public void close() throws IOException {
        client.close();
    }

    public class Response {
        private final int        statusCode;
        private final JSONObject jsonContent;

        private Response(int statusCode, JSONObject jsonContent) {
            this.statusCode = statusCode;
            this.jsonContent = jsonContent;

        }

        public int statusCode() {
            return statusCode;
        }

        public JSONObject jsonContent() {
            return jsonContent;
        }
    }
}
