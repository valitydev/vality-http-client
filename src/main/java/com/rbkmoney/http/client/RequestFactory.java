package com.rbkmoney.http.client;

import org.apache.http.client.methods.HttpPost;

public interface RequestFactory<T> {

    int DEFAULT_CONNECTION_TIMEOUT = 10000;
    int DEFAULT_SOCKET_TIMEOUT = 30000;

    HttpPost create(T request, String url);

    HttpPost createHttpPostUrlParams(T request, String url, int executionTimeout);

    HttpPost createHttpPostUrlParams(T request, String url, int timeout, int executionTimeout);
}
