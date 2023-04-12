package dev.vality.http.client.factory.configurer;

import dev.vality.http.client.exception.ClientCreationException;
import dev.vality.http.client.properties.ClientPoolRequestConfig;
import dev.vality.http.client.properties.SslRequestConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
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
public class SslHttpClientConfigurer implements HttpClientConfigurer {

    @Override
    public void configure(HttpClientBuilder httpClientBuilder, ClientPoolRequestConfig commonConfig) {
        SslRequestConfig config = commonConfig.getSslRequestConfig();
        try {
            SSLContext sslContext =
                    createSslContext(config.getCertPath(), config.getCertType(), config.getCertPass());
            httpClientBuilder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                    .setSSLContext(sslContext);
        } catch (Exception e) {
            log.error("Error when HttpClientFactory create e: ", e);
            throw new ClientCreationException(e);
        }
    }

    @Override
    public boolean isApplicable(ClientPoolRequestConfig config) {
        return config.getSslRequestConfig() != null;
    }

    private KeyStore createKeyStore(String type, String certificate, String password)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException {
        KeyStore keyStore = KeyStore.getInstance(type);
        try (InputStream pKeyFileStream = Files.newInputStream(Paths.get(certificate))) {
            keyStore.load(pKeyFileStream, password.toCharArray());
        }
        return keyStore;
    }

    private SSLContext createSslContext(String certFilePath, String certType, String certPass)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = createKeyStore(certType, certFilePath, certPass);
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
