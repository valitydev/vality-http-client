package dev.vality.http.client.factory;

import dev.vality.http.client.properties.KeyStoreProperties;
import dev.vality.http.client.properties.SslRequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;

public class HttpClientFactoryTest {

    HttpClientFactory httpClientFactory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        KeyStoreProperties keyStoreProperties = new KeyStoreProperties();
        keyStoreProperties.setCertificateFolder("./src/test/resources/");
        keyStoreProperties.setPassword("12345");
        keyStoreProperties.setType("pkcs12");
        httpClientFactory = new HttpClientFactory(1, 1, 1, 1, 1, keyStoreProperties);
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
                .certFileName("rbkmoney.p12")
                .build());

        assertNotNull(closeableHttpClient);
    }
}