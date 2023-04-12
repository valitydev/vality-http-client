package dev.vality.http.client.factory;

import dev.vality.http.client.factory.configurer.HttpClientConfigurer;
import dev.vality.http.client.factory.configurer.SslHttpClientConfigurer;
import dev.vality.http.client.properties.ClientPoolRequestConfig;
import dev.vality.http.client.properties.SslRequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class HttpClientFactoryTest {

    HttpClientFactory httpClientFactory;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        List<HttpClientConfigurer> configurers = List.of(new SslHttpClientConfigurer());
        httpClientFactory = new HttpClientFactory(1, 1, 1, 1, 1, configurers);
    }

    @Test
    public void create() {
        CloseableHttpClient closeableHttpClient = httpClientFactory.create();

        assertNotNull(closeableHttpClient);
    }

    @Test
    public void testCreate() {
        CloseableHttpClient closeableHttpClient =
                httpClientFactory.create(ClientPoolRequestConfig.builder()
                        .sslRequestConfig(SslRequestConfig.builder()
                                .certType("pkcs12")
                                .certPass("12345")
                                .certPath("./src/test/resources/vality.p12")
                                .build())
                        .build());

        assertNotNull(closeableHttpClient);
    }
}