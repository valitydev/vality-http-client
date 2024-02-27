package dev.vality.http.client;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;

import java.util.concurrent.Future;

public interface AsyncHttpClient<C> {

    Future<HttpResponse> post(String methodName, HttpPost httpPost, FutureCallback<HttpResponse> callback, C client);

    Future<HttpResponse> post(String methodName, HttpPost httpPost, FutureCallback<HttpResponse> callback);

    Future<HttpResponse> get(String methodName, HttpGet httpGet, FutureCallback<HttpResponse> callback, C client);

    Future<HttpResponse> get(String methodName, HttpGet httpGet, FutureCallback<HttpResponse> callback);

    Future<HttpResponse> delete(String methodName,
                                HttpDelete httpDelete,
                                FutureCallback<HttpResponse> callback,
                                C client);

    Future<HttpResponse> delete(String methodName, HttpDelete httpDelete, FutureCallback<HttpResponse> callback);

    Future<HttpResponse> put(String methodName, HttpPut httpPut, FutureCallback<HttpResponse> callback, C client);

    Future<HttpResponse> put(String methodName, HttpPut httpPut, FutureCallback<HttpResponse> callback);

}
