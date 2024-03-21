package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.ProxyRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.Timeout;

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
            PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = initConnectionManagerBuilder();
            connectionManagerBuilder.setSSLSocketFactory(initSslSocketFactory());
            if (needProxy(config)) {
                HttpHost proxy = new HttpHost("http", config.getAddress(), config.getPort());
                httpClientBuilder.setProxy(proxy);

                if (needAuth(config)) {
                    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(new AuthScope(config.getAddress(), config.getPort()),
                            new UsernamePasswordCredentials(config.getUser(), config.getPassword().toCharArray()));
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            }
            return httpClientBuilder
                    .setConnectionManager(connectionManagerBuilder.build())
                    .build();
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

    private boolean needProxy(ProxyRequestConfig config) {
        return config != null && config.getKey() != null && config.getAddress() != null;
    }

    private boolean needAuth(ProxyRequestConfig config) {
        return config != null && config.getUser() != null && config.getPassword() != null;
    }

    private HttpClientBuilder initHttpClientBuilder() {
        RequestConfig config = createDefaultRequestConfig();
        return HttpClients.custom()
                .setDefaultRequestConfig(config)
                .disableAutomaticRetries();
    }

    private PoolingHttpClientConnectionManagerBuilder initConnectionManagerBuilder() {
        return PoolingHttpClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(maxPerRoute)
                .setMaxConnTotal(maxTotal)
                .setDefaultConnectionConfig(createDefaultConnectionConfig());
    }

    @SneakyThrows
    private LayeredConnectionSocketFactory initSslSocketFactory() {
        return SSLConnectionSocketFactoryBuilder.create()
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
    }

    private ConnectionConfig createDefaultConnectionConfig() {
        return ConnectionConfig.custom()
                .setConnectTimeout(Timeout.ofMilliseconds(connectionTimeout))
                .setSocketTimeout(Timeout.ofMilliseconds(requestTimeout))
                .build();
    }

    private RequestConfig createDefaultRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(poolTimeout))
                .build();
    }

}
