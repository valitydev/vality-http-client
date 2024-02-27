package dev.vality.http.client;

import dev.vality.http.client.domain.Response;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.http.HttpResponse;

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
