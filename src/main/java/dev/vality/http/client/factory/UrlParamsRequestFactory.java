package dev.vality.http.client.factory;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class UrlParamsRequestFactory implements RequestFactory<Map<String, String>> {

    @Override
    public HttpPost create(Map<String, String> params, String url) {
        return createHttpPostUrlParams(params, url, DEFAULT_SOCKET_TIMEOUT_MS);
    }

    @Override
    public HttpPost createHttpPostUrlParams(Map<String, String> request, String url, int executionTimeout) {
        return createHttpPostUrlParams(request, url, DEFAULT_CONNECTION_TIMEOUT_MS, executionTimeout);
    }

    @Override
    public HttpPost createHttpPostUrlParams(Map<String, String> request,
                                            String url,
                                            int timeout,
                                            int executionTimeout) {
        HttpPost post = new HttpPost(url);
        post.setConfig(RequestConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(timeout))
                //TODO: how to set or not used at all?
                //.setSocketTimeout(executionTimeout)
                .build());
        List<NameValuePair> urlParams = request.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        post.setEntity(new UrlEncodedFormEntity(urlParams, StandardCharsets.UTF_8));
        return post;
    }

    private PoolingHttpClientConnectionManagerBuilder initConnectionManagerBuilder(int connectionTimeoutMs, int socketTimeoutMs) {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setDefaultConnectionConfig(createDefaultConnectionConfig(connectionTimeoutMs, socketTimeoutMs));
    }

    private ConnectionConfig createDefaultConnectionConfig(int connectionTimeoutMs, int socketTimeoutMs) {
        return ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeoutMs))
                .setSocketTimeout(Timeout.ofMilliseconds(socketTimeoutMs))
                .build();
    }

}
