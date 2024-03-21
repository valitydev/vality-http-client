package dev.vality.http.client;

import dev.vality.http.client.exception.RemoteInvocationException;
import dev.vality.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

@Slf4j
@Builder
public class HttpClientImpl implements HttpClient<CloseableHttpClient> {

    private MeterRegistry registry;
    private boolean isEnableMetric;
    private CloseableHttpClient client;

    @Override
    public <T> T post(String methodName,
                                HttpPost httpPost,
                                HttpClientResponseHandler<T> handler,
                                CloseableHttpClient client) {
        return httpExecution(methodName, httpPost, handler, client);
    }

    @Override
    public <T> T post(String methodName, HttpPost httpPost, HttpClientResponseHandler<T> handler) {
        return httpExecution(methodName, httpPost, handler, client);
    }

    @Override
    public <T> T get(String methodName,
                               HttpGet httpGet,
                               HttpClientResponseHandler<T> handler,
                               CloseableHttpClient client) {
        return httpExecution(methodName, httpGet, handler, client);
    }

    @Override
    public <T> T get(String methodName, HttpGet httpGet, HttpClientResponseHandler<T> handler) {
        return httpExecution(methodName, httpGet, handler, client);
    }

    @Override
    public <T> T delete(String methodName,
                                  HttpDelete httpDelete,
                                  HttpClientResponseHandler<T> handler,
                                  CloseableHttpClient client) {
        return httpExecution(methodName, httpDelete, handler, client);
    }

    @Override
    public <T> T delete(String methodName,
                                  HttpDelete httpDelete,
                                  HttpClientResponseHandler<T> handler) {
        return httpExecution(methodName, httpDelete, handler, client);
    }

    @Override
    public <T> T put(String methodName,
                               HttpPut httpPut,
                               HttpClientResponseHandler<T> handler,
                               CloseableHttpClient client) {
        return httpExecution(methodName, httpPut, handler, client);
    }

    @Override
    public <T> T put(String methodName, HttpPut httpPut, HttpClientResponseHandler<T> handler) {
        return httpExecution(methodName, httpPut, handler, client);
    }

    private <T> T httpExecution(String methodName,
                                HttpUriRequestBase httpRequestBase,
                                HttpClientResponseHandler<T> handler,
                                CloseableHttpClient client) {
        if (client == null) {
            log.error("SimpleHttpClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            //TODO: Return samplers and logging
            return client.execute(httpRequestBase, handler);
        } catch (Exception e) {
            log.error("Error when httpExecution e: ", e);
            throw new RemoteInvocationException(e);
        }
    }

}
