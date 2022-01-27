package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.KeyStoreProperties;
import dev.vality.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.ssl.SSLContextBuilder;

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
            SSLContext sslContext =
                    createSslContext(config.getCertFileName(), config.getCertType(), config.getCertPass());
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(sslContext);
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
            SSLContext sslContext =
                    createSslContext(config.getCertFileName(), config.getCertType(), config.getCertPass());
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(sslContext);
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
