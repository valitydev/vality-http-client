package dev.vality.http.client.pool;

import dev.vality.http.client.factory.HttpClientFactory;
import dev.vality.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class SslCertHttpClientPool implements HttpClientPool<SslRequestConfig, CloseableHttpClient> {

    private final HttpClientFactory httpClientFactory;
    private final Function<SslRequestConfig, String> keyGeneratorFunction;

    private Map<String, CloseableHttpClient> pool = new ConcurrentHashMap<>();

    @Override
    public CloseableHttpClient get(SslRequestConfig config) {
        return pool.computeIfAbsent(keyGeneratorFunction.apply(config), s -> httpClientFactory.create(config));
    }

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
