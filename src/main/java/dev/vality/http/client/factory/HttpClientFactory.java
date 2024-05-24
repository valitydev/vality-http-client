package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.KeyStoreProperties;
import dev.vality.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.LayeredConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

@Slf4j
@RequiredArgsConstructor
public class HttpClientFactory {

    private final int requestTimeout;
    private final int poolTimeout;
    private final int connectionTimeout;
    private final int maxPerRoute;
    private final int maxTotal;

    private final KeyStoreProperties keyStoreProperties;

    public CloseableHttpClient create(SslRequestConfig config) {
        try {
            HttpClientBuilder httpClientBuilder = initHttpClientBuilder();
            PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = initConnectionManagerBuilder();
            connectionManagerBuilder.setSSLSocketFactory(initSslSocketFactory(config));
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
            PoolingHttpClientConnectionManagerBuilder connectionManagerBuilder = initConnectionManagerBuilder();
            return httpClientBuilder
                    .setConnectionManager(connectionManagerBuilder.build())
                    .build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
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
    private LayeredConnectionSocketFactory initSslSocketFactory(SslRequestConfig config) {
        return SSLConnectionSocketFactoryBuilder.create()
                .setHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .setSslContext(createSslContext(config.getCertFileName(), config.getCertType(), config.getCertPass()))
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

    private KeyStore createKeyStore(String type, String certificate, String password)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(type);
        try (InputStream pKeyFileStream = Files.newInputStream(Paths.get(certificate))) {
            keyStore.load(pKeyFileStream, password.toCharArray());
        }
        return keyStore;
    }

    private SSLContext createSslContext(String certFileName, String certType, String certPass)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        String certificate = keyStoreProperties.getCertificateFolder() + certFileName;
        KeyStore keyStore = createKeyStore(certType, certificate, certPass);
        return createSslContext(keyStore, certPass);
    }

    private SSLContext createSslContext(KeyStore keyStore, String password)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        return new SSLContextBuilder()
                .loadTrustMaterial(keyStore, (x509Certificates, s) -> true)
                .loadKeyMaterial(keyStore, password.toCharArray())
                .build();
    }

}
