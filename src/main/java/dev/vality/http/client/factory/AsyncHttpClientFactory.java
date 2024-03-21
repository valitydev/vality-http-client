package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.KeyStoreProperties;
import dev.vality.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.nio.ssl.TlsStrategy;
import org.apache.hc.core5.reactor.IOReactorConfig;
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
public class AsyncHttpClientFactory {

    private final int requestTimeout;
    private final int poolTimeout;
    private final int connectionTimeout;
    private final int maxPerRoute;
    private final int maxTotal;

    private final int keepAliveMs;

    //by default availableProcessors
    private final int ioReactorNumber;

    private final KeyStoreProperties keyStoreProperties;

    public CloseableHttpAsyncClient create(SslRequestConfig config) {
        try {
            HttpAsyncClientBuilder httpClientBuilder = initHttpClientBuilder();
            PoolingAsyncClientConnectionManagerBuilder connectionManagerBuilder = initConnectionManagerBuilder();
            connectionManagerBuilder.setTlsStrategy(initTlsStrategy(config));
            httpClientBuilder.setConnectionManager(connectionManagerBuilder.build());
            return httpClientBuilder.build();
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    public CloseableHttpAsyncClient create(SslRequestConfig config, ConnectionReuseStrategy connectionReuseStrategy) {
        try {
            HttpAsyncClientBuilder httpClientBuilder = initHttpClientBuilder();
            httpClientBuilder.setConnectionReuseStrategy(connectionReuseStrategy);
            PoolingAsyncClientConnectionManagerBuilder connectionManagerBuilder = initConnectionManagerBuilder();
            connectionManagerBuilder.setTlsStrategy(initTlsStrategy(config));
            httpClientBuilder.setConnectionManager(connectionManagerBuilder.build());
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
                .setIOReactorConfig(initReactor())
                .setKeepAliveStrategy((response, context) -> Timeout.ofMilliseconds(keepAliveMs))
                .setDefaultRequestConfig(createDefaultRequestConfig());
    }

    private IOReactorConfig initReactor() {
        return IOReactorConfig.custom()
                .setIoThreadCount(ioReactorNumber > 0 ? ioReactorNumber : Runtime.getRuntime().availableProcessors())
                .setSoTimeout(Timeout.ofMilliseconds(requestTimeout))
                .build();
    }

    private RequestConfig createDefaultRequestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofMilliseconds(poolTimeout))
                .build();
    }

    private PoolingAsyncClientConnectionManagerBuilder initConnectionManagerBuilder() {
        return PoolingAsyncClientConnectionManagerBuilder.create()
                .setMaxConnPerRoute(maxPerRoute)
                .setMaxConnTotal(maxTotal)
                .setDefaultConnectionConfig(createDefaultConnectionConfig());
    }

    @SneakyThrows
    private TlsStrategy initTlsStrategy(SslRequestConfig config) {
        return ClientTlsStrategyBuilder
                .create()
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

    private KeyStore createKeyStore(String type, String certificate, String password)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(type);
        try (InputStream pKeyFileStream = Files.newInputStream(Paths.get(certificate))) {
            keyStore.load(pKeyFileStream, password.toCharArray());
        }
        return keyStore;
    }

    private SSLContext createSslContext(String certFileName, String certType, String certPass)
            throws KeyStoreException, NoSuchAlgorithmException, IOException,
            CertificateException, UnrecoverableKeyException, KeyManagementException {
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
