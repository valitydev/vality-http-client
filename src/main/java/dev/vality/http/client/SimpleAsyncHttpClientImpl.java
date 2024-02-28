package dev.vality.http.client;

import dev.vality.http.client.exception.RemoteInvocationException;
import dev.vality.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;

import java.util.concurrent.Future;

@Slf4j
@Builder
public class SimpleAsyncHttpClientImpl implements SimpleAsyncHttpClient<CloseableHttpAsyncClient> {

    private MeterRegistry registry;
    private boolean isEnableMetric;
    private CloseableHttpAsyncClient client;

    @Override
    public Future<SimpleHttpResponse> execute(String methodName,
                                              SimpleHttpRequest httpPost,
                                              FutureCallback<SimpleHttpResponse> callback,
                                              CloseableHttpAsyncClient client) {
        return httpExecution(methodName, httpPost, callback, client);
    }

    @Override
    public Future<SimpleHttpResponse> execute(String methodName,
                                              SimpleHttpRequest httpPost,
                                              FutureCallback<SimpleHttpResponse> callback) {
        return httpExecution(methodName, httpPost, callback, client);
    }

    private Future<SimpleHttpResponse> httpExecution(String methodName,
                                                     SimpleHttpRequest request,
                                                     FutureCallback<SimpleHttpResponse> callback,
                                                     CloseableHttpAsyncClient client) {
        if (client == null) {
            log.error("CloseableHttpAsyncClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            Timer.Sample sample = startSampleTimer();
            client.start();
            Future<SimpleHttpResponse> execute = client.execute(request, callback);
            finishSampleTimer(request.getMethod(), methodName, sample);

            log.debug("HttpClient finish methodType: {} methodName: {} httpGet: {} ",
                    request.getMethod(), methodName, request);
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
