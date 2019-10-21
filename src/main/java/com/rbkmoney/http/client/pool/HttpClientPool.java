package com.rbkmoney.http.client.pool;

import org.apache.http.impl.client.CloseableHttpClient;

import java.util.function.Function;

public interface HttpClientPool<T> {

    CloseableHttpClient get(T config);

    void destroy();

}
