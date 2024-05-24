package dev.vality.http.client;

import dev.vality.http.client.exception.RemoteInvocationException;
import dev.vality.http.client.exception.UnknownClientException;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;

import java.util.concurrent.Future;

@Slf4j
@Builder
public class AsyncHttpClientImpl implements AsyncHttpClient<CloseableHttpAsyncClient> {

    private MeterRegistry registry;
    private boolean isEnableMetric;
    private CloseableHttpAsyncClient client;

    @Override
    public <T> Future<T> execute(String methodName,
                                 AsyncRequestProducer asyncRequestProducer,
                                 AsyncResponseConsumer<T> responseConsumer,
                                 FutureCallback<T> callback,
                                 CloseableHttpAsyncClient client) {
        return httpExecution(methodName, asyncRequestProducer, responseConsumer, callback, client);
    }

    @Override
    public <T> Future<T> execute(String methodName,
                                 AsyncRequestProducer asyncRequestProducer,
                                 AsyncResponseConsumer<T> responseConsumer,
                                 FutureCallback<T> callback) {
        return httpExecution(methodName, asyncRequestProducer, responseConsumer, callback, client);
    }

    private <T> Future<T> httpExecution(String methodName,
                                        AsyncRequestProducer asyncRequestProducer,
                                        AsyncResponseConsumer<T> responseConsumer,
                                        FutureCallback<T> callback,
                                        CloseableHttpAsyncClient client) {
        if (client == null) {
            log.error("CloseableHttpAsyncClient client is unknown!");
            throw new UnknownClientException();
        }
        try {
            //TODO: Return samplers and logging
            return client.execute(asyncRequestProducer, responseConsumer, callback);
        } catch (Exception e) {
            log.error("Error when httpExecution e: ", e);
            throw new RemoteInvocationException(e);
        }
    }

}
