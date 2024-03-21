package dev.vality.http.client;

import dev.vality.http.client.factory.ProxyHttpClientFactory;
import dev.vality.http.client.pool.HttpClientPool;
import dev.vality.http.client.pool.ProxyServerHttpClientPool;
import dev.vality.http.client.properties.ProxyRequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ProxyServerHttpClientPoolTest {

    private static final String PROXY_KEY = "proxyKey";

    @Mock
    private ProxyHttpClientFactory proxyHttpClientFactory;
    @Mock
    private CloseableHttpClient closeableHttpClient;

    HttpClientPool<ProxyRequestConfig, CloseableHttpClient> httpClientPool;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        httpClientPool = new ProxyServerHttpClientPool(proxyHttpClientFactory, ProxyRequestConfig::getKey);
    }

    @Test
    public void get() throws IOException {
        when(proxyHttpClientFactory.create(any())).thenReturn(closeableHttpClient);

        ProxyRequestConfig proxyRequestConfig = ProxyRequestConfig.builder()
                .key(PROXY_KEY)
                .build();
        CloseableHttpClient closeableHttpClientRes = httpClientPool.get(proxyRequestConfig);

        verify(proxyHttpClientFactory, timeout(1)).create(any());
        assertEquals(closeableHttpClient, closeableHttpClientRes);

        httpClientPool.get(proxyRequestConfig);
        httpClientPool.get(proxyRequestConfig);

        verify(proxyHttpClientFactory, timeout(1)).create(proxyRequestConfig);
        assertEquals(closeableHttpClient, closeableHttpClientRes);

        httpClientPool.destroy();

        verify(closeableHttpClientRes, times(1)).close();
    }

}

