package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.ProxyRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
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
            if (needProxy(config)) {
                HttpHost proxy = new HttpHost(config.getAddress(), config.getPort(), "http");
                httpClientBuilder.setProxy(proxy);

                if (needAuth(config)) {
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(new AuthScope(config.getAddress(), config.getPort()),
                            new UsernamePasswordCredentials(config.getUser(), config.getPassword()));
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
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

    private boolean needProxy(ProxyRequestConfig config) {
        return config != null && config.getKey() != null && config.getAddress() != null;
    }

    private boolean needAuth(ProxyRequestConfig config) {
        return config != null && config.getUser() != null && config.getPassword() != null;
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
