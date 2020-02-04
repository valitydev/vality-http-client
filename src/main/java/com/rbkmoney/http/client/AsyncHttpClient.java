package com.rbkmoney.http.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.concurrent.Future;

public interface AsyncHttpClient<C> {

    Future<HttpResponse> post(String methodName, HttpPost httpPost, C client);

    Future<HttpResponse> post(String methodName, HttpPost httpPost);

    Future<HttpResponse> get(String methodName, HttpGet httpGet, C client);

    Future<HttpResponse> get(String methodName, HttpGet httpGet);

    Future<HttpResponse> delete(String methodName, HttpDelete httpDelete, C client);

    Future<HttpResponse> delete(String methodName, HttpDelete httpDelete);

    Future<HttpResponse> put(String methodName, HttpPut httpPut, C client);

    Future<HttpResponse> put(String methodName, HttpPut httpPut);

}
