package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.KeyStoreProperties;
import dev.vality.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
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
