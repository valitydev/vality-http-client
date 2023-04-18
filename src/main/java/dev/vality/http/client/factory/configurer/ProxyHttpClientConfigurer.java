package dev.vality.http.client.factory.configurer;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.RequestConfig;
import dev.vality.http.client.properties.ProxyRequestConfig;
import dev.vality.http.client.validator.ProxyRequestConfigValidator;
import dev.vality.http.client.validator.RequestConfigValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;

@Slf4j
public class ProxyHttpClientConfigurer implements HttpClientConfigurer {

    private final RequestConfigValidator<ProxyRequestConfig> validator = new ProxyRequestConfigValidator();

    @Override
    public void configure(HttpClientBuilder httpClientBuilder, RequestConfig commonConfig) {
        ProxyRequestConfig config = commonConfig.getProxyRequestConfig();
        validator.validate(config);
        try {
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
            HttpHost proxy = new HttpHost(config.getAddress(), config.getPort(), "http");
            httpClientBuilder.setProxy(proxy);

            if (needAuth(config)) {
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(new AuthScope(config.getAddress(), config.getPort()),
                        new UsernamePasswordCredentials(config.getUser(), config.getPassword()));
                httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    @Override
    public boolean isApplicable(RequestConfig config) {
        return config.getProxyRequestConfig() != null;
    }

    private boolean needAuth(ProxyRequestConfig config) {
        return config != null && config.getUser() != null && config.getPassword() != null;
    }
}
