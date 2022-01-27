package dev.vality.http.client;

import dev.vality.http.client.domain.Response;
import dev.vality.http.client.exception.RemoteInvocationException;
import dev.vality.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.function.Function;

@Slf4j
@Builder
public class SimpleHttpClient implements HttpClient<CloseableHttpClient, CloseableHttpResponse> {

    private MeterRegistry registry;
    private boolean isEnableMetric;
    private CloseableHttpClient client;

    @Override
    public <T> Response<T> post(String methodName,
                                HttpPost httpPost,
                                Function<CloseableHttpResponse, T> handler,
                                CloseableHttpClient client) {
        return httpExecution(methodName, httpPost, handler, client);
    }

    @Override
    public <T> Response<T> post(String methodName, HttpPost httpPost, Function<CloseableHttpResponse, T> handler) {
        return httpExecution(methodName, httpPost, handler, client);
    }

    @Override
    public <T> Response<T> get(String methodName,
                               HttpGet httpGet,
                               Function<CloseableHttpResponse, T> handler,
                               CloseableHttpClient client) {
        return httpExecution(methodName, httpGet, handler, client);
    }

    @Override
    public <T> Response<T> get(String methodName, HttpGet httpGet, Function<CloseableHttpResponse, T> handler) {
        return httpExecution(methodName, httpGet, handler, client);
    }

    @Override
    public <T> Response<T> delete(String methodName,
                                  HttpDelete httpDelete,
                                  Function<CloseableHttpResponse, T> handler,
                                  CloseableHttpClient client) {
        return httpExecution(methodName, httpDelete, handler, client);
    }

    @Override
    public <T> Response<T> delete(String methodName,
                                  HttpDelete httpDelete,
                                  Function<CloseableHttpResponse, T> handler) {
        return httpExecution(methodName, httpDelete, handler, client);
    }

    @Override
    public <T> Response<T> put(String methodName,
                               HttpPut httpPut,
                               Function<CloseableHttpResponse, T> handler,
                               CloseableHttpClient client) {
        return httpExecution(methodName, httpPut, handler, client);
    }

    @Override
    public <T> Response<T> put(String methodName, HttpPut httpPut, Function<CloseableHttpResponse, T> handler) {
        return httpExecution(methodName, httpPut, handler, client);
    }

    private <T> Response<T> httpExecution(String methodName,
                                          HttpRequestBase httpRequestBase,
                                          Function<CloseableHttpResponse, T> handler,
                                          CloseableHttpClient client) {
        if (client == null) {
            log.error("SimpleHttpClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            Timer.Sample sample = startSampleTimer();

            try (CloseableHttpResponse response = client.execute(httpRequestBase)) {
                String methodType = httpRequestBase.getMethod();

                finishSampleTimer(methodType, methodName, sample, response);

                T result = handler.apply(response);
                log.debug("HttpClient finish methodType: {} methodName: {} httpGet: {}  with result: {}",
                        methodType, methodName, httpRequestBase, result);
                return new Response<>(result);
            }
        } catch (Exception e) {
            log.error("Error when httpExecution e: ", e);
            throw new RemoteInvocationException(e);
        }
    }

    private void finishSampleTimer(String methodType,
                                   String methodName,
                                   Timer.Sample sample,
                                   CloseableHttpResponse response) {
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
