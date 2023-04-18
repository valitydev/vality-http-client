package dev.vality.http.client.pool;

import dev.vality.http.client.factory.HttpClientFactory;
import dev.vality.http.client.properties.RequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class DefaultHttpClientPool implements HttpClientPool<RequestConfig, CloseableHttpClient> {

    private final HttpClientFactory httpClientFactory;
    private final Function<RequestConfig, String> keyGeneratorFunction;

    private Map<String, CloseableHttpClient> pool = new ConcurrentHashMap<>();

    public CloseableHttpClient get(RequestConfig requestConfig) {
        return pool.computeIfAbsent(keyGeneratorFunction.apply(requestConfig),
                s -> httpClientFactory.create(requestConfig));
    }

    @Override
    public void destroy() {
        pool.values().forEach(closeableHttpClient -> {
            try {
                closeableHttpClient.close();
            } catch (IOException e) {
                log.error("Error when close HttpClientPool e: ", e);
            }
        });
    }

}
