package dev.vality.http.client;

import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;


public interface HttpClient<C> {

    <T> T post(String methodName, HttpPost httpPost, HttpClientResponseHandler<T> handler,
                         C client);

    <T> T post(String methodName, HttpPost httpPost, HttpClientResponseHandler<T> handler);

    <T> T get(String methodName, HttpGet httpGet, HttpClientResponseHandler<T> handler,
                        C client);

    <T> T get(String methodName, HttpGet httpGet, HttpClientResponseHandler<T> handler);

    <T> T delete(String methodName, HttpDelete httpDelete, HttpClientResponseHandler<T> handler,
                           C client);

    <T> T delete(String methodName, HttpDelete httpDelete, HttpClientResponseHandler<T> handler);

    <T> T put(String methodName, HttpPut httpPut, HttpClientResponseHandler<T> handler,
                        C client);

    <T> T put(String methodName, HttpPut httpPut, HttpClientResponseHandler<T> handler);

}
