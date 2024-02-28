package dev.vality.http.client;

import dev.vality.http.client.domain.Response;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.core5.http.HttpResponse;

import java.util.function.Function;

public interface HttpClient<C, R extends HttpResponse> {

    <T> Response<T> execute(String methodName, SimpleHttpRequest request, Function<R, T> handler,
                            C client);

    <T> Response<T> execute(String methodName, SimpleHttpRequest request, Function<R, T> handler);

}
