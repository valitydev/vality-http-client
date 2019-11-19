package com.rbkmoney.http.client.pool;

import com.rbkmoney.http.client.factory.AsyncHttpClientFactory;
import com.rbkmoney.http.client.factory.HttpClientFactory;
import com.rbkmoney.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class SslCertAsyncHttpClientPool implements HttpClientPool<SslRequestConfig, CloseableHttpAsyncClient> {

    private final AsyncHttpClientFactory httpClientFactory;
    private final Function<SslRequestConfig, String> keyGeneratorFunction;

    private Map<String, CloseableHttpAsyncClient> pool = new ConcurrentHashMap<>();

    @Override
    public CloseableHttpAsyncClient get(SslRequestConfig config) {
        return pool.computeIfAbsent(keyGeneratorFunction.apply(config), s -> httpClientFactory.create(config));
    }

    public void destroy() {
        pool.values().forEach(closeableHttpClient -> {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                log.error("Error when close HttpClientPool e: ", e);
            }
        });
    }

}
