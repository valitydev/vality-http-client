package com.rbkmoney.http.client;

import com.rbkmoney.http.client.callback.LogFutureCallback;
import com.rbkmoney.http.client.domain.Response;
import com.rbkmoney.http.client.exception.RemoteInvocationException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import java.util.concurrent.Future;
import java.util.function.Function;

@Slf4j
@Builder
public class SyncAsyncHttpClient implements HttpClient<CloseableHttpAsyncClient, HttpResponse> {

    private MeterRegistry registry;
    private boolean isEnableMetric;

    @Override
    public <T> Response<T> post(String methodName,
                                HttpPost httpPost,
                                Function<HttpResponse, T> handler,
                                CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPost, handler, client);
    }

    @Override
    public <T> Response<T> get(String methodName,
                               HttpGet httpGet,
                               Function<HttpResponse, T> handler,
                               CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpGet, handler, client);
    }

    @Override
    public <T> Response<T> delete(String methodName,
                                  HttpDelete httpDelete,
                                  Function<HttpResponse, T> handler,
                                  CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpDelete, handler, client);
    }

    @Override
    public <T> Response<T> put(String methodName,
                               HttpPut httpPut,
                               Function<HttpResponse, T> handler,
                               CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPut, handler, client);
    }

    private <T> Response<T> httpExecution(String methodName,
                                          HttpRequestBase httpRequestBase,
                                          Function<HttpResponse, T> handler,
                                          CloseableHttpAsyncClient client) {
        try {
            Timer.Sample sample = startSampleTimer();

            if (!client.isRunning()) {
                client.start();
            }

            Future<HttpResponse> execute = client.execute(httpRequestBase, new LogFutureCallback(httpRequestBase));

            String methodType = httpRequestBase.getMethod();
            HttpResponse httpResponse = execute.get();
            finishSampleTimer(methodType, methodName, sample, httpResponse);

            T result = handler.apply(httpResponse);

            log.debug("HttpClient finish methodType: {} methodName: {} httpGet: {}  with result: {}", methodType, methodName,
                    httpRequestBase, result);
            return new Response<>(result);
        } catch (Exception e) {
            log.error("Error when httpExecution e: ", e);
            throw new RemoteInvocationException(e);
        }

    }

    private void finishSampleTimer(String methodType, String methodName, Timer.Sample sample, HttpResponse response) {
        if (isEnableMetric && response != null && response.getStatusLine() != null && sample != null) {
            sample.stop(registry.timer(methodType, methodName,
                    String.valueOf(response.getStatusLine().getStatusCode())));
        }
    }

    private Timer.Sample startSampleTimer() {
        if (isEnableMetric && registry != null) {
            return Timer.start(registry);
        }
        return null;
    }

}
