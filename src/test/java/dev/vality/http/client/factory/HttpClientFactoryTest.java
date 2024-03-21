package dev.vality.http.client.factory;

import dev.vality.http.client.properties.KeyStoreProperties;
import dev.vality.http.client.properties.SslRequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpClientFactoryTest {

    HttpClientFactory httpClientFactory;
    AutoCloseable mocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);

        KeyStoreProperties keyStoreProperties = new KeyStoreProperties();
        keyStoreProperties.setCertificateFolder("./src/test/resources/");
        keyStoreProperties.setPassword("12345");
        keyStoreProperties.setType("pkcs12");
        httpClientFactory = new HttpClientFactory(1, 1, 1, 1, 1, keyStoreProperties);
    }

    @AfterEach
    public void cleanUp() throws Exception {
        mocks.close();
    }

    @Test
    public void create() {
        CloseableHttpClient closeableHttpClient = httpClientFactory.create();

        assertNotNull(closeableHttpClient);
    }

    @Test
    public void testCreate() {
        CloseableHttpClient closeableHttpClient = httpClientFactory.create(SslRequestConfig.builder()
                .certType("pkcs12")
                .certPass("12345")
                .certFileName("vality.p12")
                .build());

        assertNotNull(closeableHttpClient);
    }
}