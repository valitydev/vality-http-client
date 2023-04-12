package dev.vality.http.client.factory.configurer.async;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.ClientPoolRequestConfig;
import dev.vality.http.client.properties.ProxyRequestConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

@Slf4j
public class ProxyAsyncHttpClientConfigurer implements AsyncHttpClientConfigurer {
    @Override
    public void configure(HttpAsyncClientBuilder httpClientBuilder, ClientPoolRequestConfig commonConfig) {
        ProxyRequestConfig config = commonConfig.getProxyRequestConfig();
        try {
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
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    @Override
    public boolean isApplicable(ClientPoolRequestConfig config) {
        return config.getProxyRequestConfig() != null;
    }

    private boolean needProxy(ProxyRequestConfig config) {
        return config != null && config.getKey() != null && config.getAddress() != null;
    }

    private boolean needAuth(ProxyRequestConfig config) {
        return config != null && config.getUser() != null && config.getPassword() != null;
    }
}
