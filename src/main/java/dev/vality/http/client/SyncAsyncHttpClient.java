package dev.vality.http.client;

import dev.vality.http.client.callback.LogFutureCallback;
import dev.vality.http.client.domain.Response;
import dev.vality.http.client.exception.RemoteInvocationException;
import dev.vality.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.http.HttpResponse;

import java.util.concurrent.Future;
import java.util.function.Function;

@Slf4j
@Builder
public class SyncAsyncHttpClient implements HttpClient<CloseableHttpAsyncClient, HttpResponse> {

    private MeterRegistry registry;
    private boolean isEnableMetric;
    private CloseableHttpAsyncClient client;

    @Override
    public <T> Response<T> execute(String methodName,
                                   SimpleHttpRequest request,
                                   Function<HttpResponse, T> handler,
                                   CloseableHttpAsyncClient client) {
        return httpExecution(methodName, request, handler, client);
    }

    @Override
    public <T> Response<T> execute(String methodName, SimpleHttpRequest request, Function<HttpResponse, T> handler) {
        return httpExecution(methodName, request, handler, client);
    }

    private <T> Response<T> httpExecution(String methodName,
                                          SimpleHttpRequest httpRequestBase,
                                          Function<HttpResponse, T> handler,
                                          CloseableHttpAsyncClient client) {
        if (client == null) {
            log.error("SimpleHttpClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            Timer.Sample sample = startSampleTimer();

            client.start();

            Future<SimpleHttpResponse> execute = client.execute(httpRequestBase, new LogFutureCallback(httpRequestBase));

            String methodType = httpRequestBase.getMethod();
            HttpResponse httpResponse = execute.get();
            finishSampleTimer(methodType, methodName, sample, httpResponse);

            T result = handler.apply(httpResponse);

            log.debug("HttpClient finish methodType: {} methodName: {} httpGet: {}  with result: {}",
                    methodType, methodName, httpRequestBase, result);
            return new Response<>(result);
        } catch (Exception e) {
            log.error("Error when httpExecution e: ", e);
            throw new RemoteInvocationException(e);
        }

    }

    private void finishSampleTimer(String methodType, String methodName, Timer.Sample sample, HttpResponse response) {
        if (isEnableMetric && response != null && sample != null) {
            sample.stop(registry.timer(methodType, methodName,
                    String.valueOf(response.getCode())));
        }
    }

    private Timer.Sample startSampleTimer() {
        if (isEnableMetric && registry != null) {
            return Timer.start(registry);
        }
        return null;
    }

}
