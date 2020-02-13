package com.rbkmoney.http.client;

import com.rbkmoney.http.client.exception.RemoteInvocationException;
import com.rbkmoney.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.concurrent.Future;

@Slf4j
@Builder
public class AsyncHttpClientImpl implements AsyncHttpClient<CloseableHttpAsyncClient> {

    private MeterRegistry registry;
    private boolean isEnableMetric;
    private CloseableHttpAsyncClient client;

    @Override
    public Future<HttpResponse> post(String methodName,
                                     HttpPost httpPost,
                                     FutureCallback<HttpResponse> callback,
                                     CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPost, callback, client);
    }

    @Override
    public Future<HttpResponse> post(String methodName,
                                     HttpPost httpPost,
                                     FutureCallback<HttpResponse> callback) {
        return httpExecution(methodName, httpPost, callback, client);
    }

    @Override
    public Future<HttpResponse> get(String methodName,
                                    HttpGet httpGet,
                                    FutureCallback<HttpResponse> callback,
                                    CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpGet, callback, client);
    }

    @Override
    public Future<HttpResponse> get(String methodName,
                                    HttpGet httpGet,
                                    FutureCallback<HttpResponse> callback) {
        return httpExecution(methodName, httpGet, callback, client);
    }

    @Override
    public Future<HttpResponse> delete(String methodName,
                                       HttpDelete httpDelete,
                                       FutureCallback<HttpResponse> callback,
                                       CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpDelete, callback, client);
    }

    @Override
    public Future<HttpResponse> delete(String methodName,
                                       HttpDelete httpDelete,
                                       FutureCallback<HttpResponse> callback) {
        return httpExecution(methodName, httpDelete, callback, client);
    }

    @Override
    public Future<HttpResponse> put(String methodName,
                                    HttpPut httpPut,
                                    FutureCallback<HttpResponse> callback,
                                    CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPut, callback, client);
    }

    @Override
    public Future<HttpResponse> put(String methodName,
                                    HttpPut httpPut,
                                    FutureCallback<HttpResponse> callback) {
        return httpExecution(methodName, httpPut, callback, client);
    }

    private Future<HttpResponse> httpExecution(String methodName,
                                               HttpRequestBase httpRequestBase,
                                               FutureCallback<HttpResponse> callback,
                                               CloseableHttpAsyncClient client) {
        if (client == null) {
            log.error("CloseableHttpAsyncClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            Timer.Sample sample = startSampleTimer();

            if (!client.isRunning()) {
                client.start();
            }

            Future<HttpResponse> execute = client.execute(httpRequestBase, callback);

            String methodType = httpRequestBase.getMethod();
            finishSampleTimer(methodType, methodName, sample);

            log.debug("HttpClient finish methodType: {} methodName: {} httpGet: {} ", methodType, methodName, httpRequestBase);
            return execute;
        } catch (Exception e) {
            log.error("Error when httpExecution e: ", e);
            throw new RemoteInvocationException(e);
        }
    }

    private void finishSampleTimer(String methodType, String methodName, Timer.Sample sample) {
        if (isEnableMetric && sample != null) {
            sample.stop(registry.timer(methodType, methodName));
        }
    }

    private Timer.Sample startSampleTimer() {
        if (isEnableMetric && registry != null) {
            return Timer.start(registry);
        }
        return null;
    }

}
