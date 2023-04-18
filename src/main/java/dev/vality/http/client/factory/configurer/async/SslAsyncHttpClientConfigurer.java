package dev.vality.http.client.factory.configurer.async;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.factory.SslContextFactory;
import dev.vality.http.client.properties.RequestConfig;
import dev.vality.http.client.properties.SslRequestConfig;
import dev.vality.http.client.validator.RequestConfigValidator;
import dev.vality.http.client.validator.SslRequestConfigValidator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import javax.net.ssl.SSLContext;

@Slf4j
@RequiredArgsConstructor
public class SslAsyncHttpClientConfigurer implements AsyncHttpClientConfigurer {

    private final RequestConfigValidator<SslRequestConfig> validator = new SslRequestConfigValidator();
    private final SslContextFactory sslContextFactory = new SslContextFactory();

    @Override
    public void configure(HttpAsyncClientBuilder httpClientBuilder, RequestConfig commonConfig) {
        SslRequestConfig config = commonConfig.getSslRequestConfig();
        validator.validate(config);
        try {
            SSLContext sslContext = sslContextFactory.createSslContext(config);
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(sslContext);
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    @Override
    public boolean isApplicable(RequestConfig config) {
        return config.getSslRequestConfig() != null;
    }

}
