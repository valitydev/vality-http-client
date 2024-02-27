package dev.vality.http.client;

import dev.vality.http.client.exception.RemoteInvocationException;
import dev.vality.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncEntityProducer;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.http.nio.entity.BasicAsyncEntityProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;

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
                                    AsyncRequestProducer asyncRequestProducer,
                                    FutureCallback<HttpResponse> callback) {
        return httpExecution(methodName, asyncRequestProducer, callback, client);
    }

    private Future<SimpleHttpResponse> httpExecution(String methodName,
                                                     HttpRequest request,
                                                     AsyncRequestProducer asyncRequestProducer,
                                                     AsyncResponseConsumer<SimpleHttpResponse> asyncResponseConsumer,
                                               FutureCallback<SimpleHttpResponse> callback,
                                               CloseableHttpAsyncClient client) {
        if (client == null) {
            log.error("CloseableHttpAsyncClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            Timer.Sample sample = startSampleTimer();
            client.start();
            AsyncEntityProducer asyncEntityProducer = new BasicAsyncEntityProducer();
            AsyncRequestProducer asyncRequestProducer1 = new BasicRequestProducer(request, asyncEntityProducer);
            Future<SimpleHttpResponse> execute = client.execute(asyncRequestProducer1,
                    asyncResponseConsumer, callback);

            String methodType = httpRequestBase.getMethod();
            finishSampleTimer(methodType, methodName, sample);

            log.debug("HttpClient finish methodType: {} methodName: {} httpGet: {} ",
                    methodType, methodName, httpRequestBase);
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
