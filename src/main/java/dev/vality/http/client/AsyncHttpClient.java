package dev.vality.http.client;

import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;

import java.util.concurrent.Future;

public interface AsyncHttpClient<C> {

    <T> Future<T> execute(String method,
                          AsyncRequestProducer asyncRequestProducer,
                          AsyncResponseConsumer<T> responseConsumer,
                          FutureCallback<T> callback,
                          C client);

    <T> Future<T> execute(String method,
                          AsyncRequestProducer asyncRequestProducer,
                          AsyncResponseConsumer<T> responseConsumer,
                          FutureCallback<T> callback);

}
