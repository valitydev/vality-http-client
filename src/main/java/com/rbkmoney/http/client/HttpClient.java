package com.rbkmoney.http.client;

import com.rbkmoney.http.client.domain.Response;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.function.Function;

public interface HttpClient {

    <T> Response<T> post(String methodName, HttpPost httpPost, Function<CloseableHttpResponse, T> handler,
                         CloseableHttpClient client);

    <T> Response<T> get(String methodName, HttpGet httpGet, Function<CloseableHttpResponse, T> handler,
                        CloseableHttpClient client);

    <T> Response<T> delete(String methodName, HttpDelete httpDelete, Function<CloseableHttpResponse, T> handler,
                           CloseableHttpClient client);

    <T> Response<T> put(String methodName, HttpPut httpPut, Function<CloseableHttpResponse, T> handler,
                        CloseableHttpClient client);
}
