package com.rbkmoney.http.client;

import com.rbkmoney.http.client.domain.Response;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.function.Function;

public interface HttpClient<C, R extends HttpResponse> {

    <T> Response<T> post(String methodName, HttpPost httpPost, Function<R, T> handler,
                         C client);

    <T> Response<T> post(String methodName, HttpPost httpPost, Function<R, T> handler);

    <T> Response<T> get(String methodName, HttpGet httpGet, Function<R, T> handler,
                        C client);

    <T> Response<T> get(String methodName, HttpGet httpGet, Function<R, T> handler);

    <T> Response<T> delete(String methodName, HttpDelete httpDelete, Function<R, T> handler,
                           C client);

    <T> Response<T> delete(String methodName, HttpDelete httpDelete, Function<R, T> handler);

    <T> Response<T> put(String methodName, HttpPut httpPut, Function<R, T> handler,
                        C client);

    <T> Response<T> put(String methodName, HttpPut httpPut, Function<R, T> handler);

}
