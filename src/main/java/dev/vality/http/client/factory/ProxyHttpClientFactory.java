package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.ProxyRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

@Slf4j
@RequiredArgsConstructor
public class ProxyHttpClientFactory {

    private final int requestTimeout;
    private final int poolTimeout;
    private final int connectionTimeout;
    private final int maxPerRoute;
    private final int maxTotal;

    public CloseableHttpClient create(ProxyRequestConfig config) {
        try {
            HttpClientBuilder httpClientBuilder = initHttpClientBuilder();
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            if (config != null && config.getKey() != null && config.getAddress() != null) {
                HttpHost proxy = new HttpHost(config.getAddress(), config.getPort(), "http");
                httpClientBuilder.setProxy(proxy);
            }
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
        RequestConfig config = createDefaultRequestConfig();
        return HttpClients.custom()
                .setMaxConnTotal(maxTotal)
                .setMaxConnPerRoute(maxPerRoute)
                .setDefaultRequestConfig(config)
                .disableAutomaticRetries();
    }

    private RequestConfig createDefaultRequestConfig() {
        return RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(poolTimeout)
                .setSocketTimeout(requestTimeout)
                .build();
    }

}