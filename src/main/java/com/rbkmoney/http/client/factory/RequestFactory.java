package com.rbkmoney.http.client.factory;

import org.apache.http.client.methods.HttpPost;

public interface RequestFactory<T> {

    int DEFAULT_CONNECTION_TIMEOUT_MS = 10000;
    int DEFAULT_SOCKET_TIMEOUT_MS = 30000;

    HttpPost create(T request, String url);

    HttpPost createHttpPostUrlParams(T request, String url, int executionTimeout);

    HttpPost createHttpPostUrlParams(T request, String url, int timeout, int executionTimeout);
}
