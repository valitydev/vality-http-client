package dev.vality.http.client.pool;

import dev.vality.http.client.factory.ProxyHttpClientFactory;
import dev.vality.http.client.properties.ProxyRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
public class ProxyServerHttpClientPool implements HttpClientPool<ProxyRequestConfig, CloseableHttpClient> {

    private final ProxyHttpClientFactory httpClientFactory;
    private final Function<ProxyRequestConfig, String> keyGeneratorFunction;

    private Map<String, CloseableHttpClient> pool = new ConcurrentHashMap<>();

    @Override
    public CloseableHttpClient get(ProxyRequestConfig config) {
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
