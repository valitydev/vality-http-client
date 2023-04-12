package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.factory.configurer.async.AsyncHttpClientConfigurer;
import dev.vality.http.client.properties.ClientPoolRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class AsyncHttpClientFactory {

    private final int requestTimeout;
    private final int poolTimeout;
    private final int connectionTimeout;
    private final int maxPerRoute;
    private final int maxTotal;

    private final int keepAliveMs;

    //by default availableProcessors
    private final int ioReactorNumber;

    private final List<AsyncHttpClientConfigurer> configurers;

    public CloseableHttpAsyncClient create(ClientPoolRequestConfig config) {
        try {
            HttpAsyncClientBuilder httpClientBuilder = initHttpClientBuilder();
            configurers.stream()
                    .filter(configurer -> configurer.isApplicable(config))
                    .forEach(configurer -> configurer.configure(httpClientBuilder, config));
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    public CloseableHttpAsyncClient create(ClientPoolRequestConfig config,
                                           ConnectionReuseStrategy connectionReuseStrategy) {
        try {
            HttpAsyncClientBuilder httpClientBuilder = initHttpClientBuilder();
            httpClientBuilder.setConnectionReuseStrategy(connectionReuseStrategy);
            configurers.stream()
                    .filter(configurer -> configurer.isApplicable(config))
                    .forEach(configurer -> configurer.configure(httpClientBuilder, config));
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    public CloseableHttpAsyncClient create(ConnectionReuseStrategy connectionReuseStrategy) {
        try {
            HttpAsyncClientBuilder httpClientBuilder = initHttpClientBuilder();
            httpClientBuilder.setConnectionReuseStrategy(connectionReuseStrategy);
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    public CloseableHttpAsyncClient create() {
        try {
            HttpAsyncClientBuilder httpClientBuilder = initHttpClientBuilder();
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    private HttpAsyncClientBuilder initHttpClientBuilder() {
        return HttpAsyncClients.custom()
                .setMaxConnTotal(maxTotal)
                .setMaxConnPerRoute(maxPerRoute)
                .setDefaultIOReactorConfig(initReactor())
                .setKeepAliveStrategy((response, context) -> keepAliveMs)
                .setDefaultRequestConfig(createDefaultRequestConfig());
    }

    private IOReactorConfig initReactor() {
        return IOReactorConfig.custom()
                .setIoThreadCount(ioReactorNumber > 0 ? ioReactorNumber : Runtime.getRuntime().availableProcessors())
                .setConnectTimeout(connectionTimeout)
                .setSoTimeout(requestTimeout)
                .build();
    }

    private RequestConfig createDefaultRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(poolTimeout)
                .setSocketTimeout(requestTimeout)
                .build();
    }

}
