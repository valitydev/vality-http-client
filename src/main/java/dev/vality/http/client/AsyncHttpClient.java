package dev.vality.http.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;

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
