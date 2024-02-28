package dev.vality.http.client;

import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.concurrent.Future;

public interface SimpleAsyncHttpClient<C> {

    Future<SimpleHttpResponse> execute(String methodName, SimpleHttpRequest httpPost, FutureCallback<SimpleHttpResponse> callback, C client);

    Future<SimpleHttpResponse> execute(String methodName, SimpleHttpRequest httpPost, FutureCallback<SimpleHttpResponse> callback);

}
