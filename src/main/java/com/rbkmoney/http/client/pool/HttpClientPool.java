package com.rbkmoney.http.client.pool;

public interface HttpClientPool<T, R> {

    R get(T config);

    void destroy();

}
