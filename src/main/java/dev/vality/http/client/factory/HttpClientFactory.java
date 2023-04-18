package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.factory.configurer.HttpClientConfigurer;
import dev.vality.http.client.properties.RequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class HttpClientFactory {

    private final int requestTimeout;
    private final int poolTimeout;
    private final int connectionTimeout;
    private final int maxPerRoute;
    private final int maxTotal;

    private final List<HttpClientConfigurer> configurers;

    public CloseableHttpClient create(RequestConfig config) {
        try {
            HttpClientBuilder httpClientBuilder = initHttpClientBuilder();
            configurers.stream()
                    .filter(configurer -> configurer.isApplicable(config))
                    .forEach(configurer -> configurer.configure(httpClientBuilder, config));
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    public CloseableHttpClient create() {
        try {
            HttpClientBuilder httpClientBuilder = initHttpClientBuilder();
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    private HttpClientBuilder initHttpClientBuilder() {
        org.apache.http.client.config.RequestConfig config = createDefaultRequestConfig();
        return HttpClients.custom()
                .setMaxConnTotal(maxTotal)
                .setMaxConnPerRoute(maxPerRoute)
                .setDefaultRequestConfig(config)
                .disableAutomaticRetries();
    }

    private org.apache.http.client.config.RequestConfig createDefaultRequestConfig() {
        return org.apache.http.client.config.RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(poolTimeout)
                .setSocketTimeout(requestTimeout)
                .build();
    }

}
