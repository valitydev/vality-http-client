package dev.vality.http.client.factory;

import dev.vality.http.client.exception.ClientConfigurationException;
import dev.vality.http.client.properties.SslRequestConfig;
import dev.vality.http.client.util.SslCertificateUtil;
import lombok.SneakyThrows;
import org.apache.http.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class SslContextFactory {

    @SneakyThrows
    public SSLContext createSslContext(SslRequestConfig config) {
        if (config.getCertFileInfo() != null) {
            SslRequestConfig.CertFileInfo certFileInfo = config.getCertFileInfo();
            return createSslContext(certFileInfo.getCertPath(), certFileInfo.getCertType(),
                    certFileInfo.getCertPass());
        }

        if (config.getCertPlainTextInfo() != null) {
            SslRequestConfig.CertPlainTextInfo certPlainTextInfo = config.getCertPlainTextInfo();
            return createSslContext(certPlainTextInfo.getPrivateCertData(), certPlainTextInfo.getPublicCertData());
        }

        throw new ClientConfigurationException("Incorrect SslRequestConfig format!");
    }

    private SSLContext createSslContext(String certFilePath, String certType, String certPass)
            throws KeyStoreException, NoSuchAlgorithmException, IOException, CertificateException,
            UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = createKeyStore(certType, certFilePath, certPass);
        return createSslContext(keyStore, certPass);
    }

    private SSLContext createSslContext(String privateKeyCertData, String publicPemCertData)
            throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, KeyManagementException {
        KeyStore keyStore = SslCertificateUtil.getKeyStore(publicPemCertData, privateKeyCertData);
        return createSslContext(keyStore, SslCertificateUtil.KEYSTORE_PASSWORD);
    }

    private SSLContext createSslContext(KeyStore keyStore, String password)
            throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        return new SSLContextBuilder()
                .loadTrustMaterial(keyStore, (x509Certificates, s) -> true)
                .loadKeyMaterial(keyStore, password.toCharArray())
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
}
