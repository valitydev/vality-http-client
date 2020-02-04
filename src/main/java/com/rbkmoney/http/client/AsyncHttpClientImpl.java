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
    private FutureCallback<HttpResponse> callback;

    @Override
    public Future<HttpResponse> post(String methodName,
                                HttpPost httpPost,
                                CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPost, client);
    }

    @Override
    public Future<HttpResponse> post(String methodName, HttpPost httpPost) {
        return httpExecution(methodName, httpPost, client);
    }

    @Override
    public Future<HttpResponse> get(String methodName,
                               HttpGet httpGet,
                               CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpGet, client);
    }

    @Override
    public Future<HttpResponse> get(String methodName, HttpGet httpGet) {
        return httpExecution(methodName, httpGet, client);
    }

    @Override
    public Future<HttpResponse> delete(String methodName,
                                  HttpDelete httpDelete,
                                  CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpDelete, client);
    }

    @Override
    public Future<HttpResponse> delete(String methodName, HttpDelete httpDelete) {
        return httpExecution(methodName, httpDelete, client);
    }

    @Override
    public Future<HttpResponse> put(String methodName,
                               HttpPut httpPut,
                               CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPut, client);
    }

    @Override
    public Future<HttpResponse> put(String methodName, HttpPut httpPut) {
        return httpExecution(methodName, httpPut, client);
    }

    private Future<HttpResponse> httpExecution(String methodName,
                                          HttpRequestBase httpRequestBase,
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
