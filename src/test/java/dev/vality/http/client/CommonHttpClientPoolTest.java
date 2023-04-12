package dev.vality.http.client;

import dev.vality.http.client.factory.HttpClientFactory;
import dev.vality.http.client.pool.CommonHttpClientPool;
import dev.vality.http.client.pool.HttpClientPool;
import dev.vality.http.client.properties.ClientPoolRequestConfig;
import dev.vality.http.client.properties.ProxyRequestConfig;
import dev.vality.http.client.properties.SslRequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CommonHttpClientPoolTest {

    private static final String PROXY_KEY = "proxyKey";
    private static final String CERT_NAME = "certName";
    HttpClientPool<ClientPoolRequestConfig, CloseableHttpClient> httpClientPool;
    @Mock
    private HttpClientFactory httpClientFactory;
    @Mock
    private CloseableHttpClient closeableHttpClient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        httpClientPool =
                new CommonHttpClientPool(httpClientFactory,
                        requestConfig -> {
                            String result = null;
                            if (requestConfig.getProxyRequestConfig() != null) {
                                result += requestConfig.getProxyRequestConfig().getKey();
                            }
                            if (requestConfig.getSslRequestConfig() != null) {
                                result += requestConfig.getSslRequestConfig().getCertPath();
                            }
                            return result;
                        });
    }

    @Test
    public void getProxy() throws IOException {
        when(httpClientFactory.create(any())).thenReturn(closeableHttpClient);

        ProxyRequestConfig proxyRequestConfig = ProxyRequestConfig.builder()
                .key(PROXY_KEY)
                .build();
        ClientPoolRequestConfig clientPoolRequestConfig = ClientPoolRequestConfig.builder()
                .proxyRequestConfig(proxyRequestConfig)
                .build();
        CloseableHttpClient closeableHttpClientRes = httpClientPool.get(clientPoolRequestConfig);

        verify(httpClientFactory, timeout(1)).create(any());
        assertEquals(closeableHttpClient, closeableHttpClientRes);

        httpClientPool.get(clientPoolRequestConfig);
        httpClientPool.get(clientPoolRequestConfig);

        verify(httpClientFactory, timeout(1)).create(clientPoolRequestConfig);
        assertEquals(closeableHttpClient, closeableHttpClientRes);

        httpClientPool.destroy();

        verify(closeableHttpClientRes, times(1)).close();
    }

    @Test
    public void getSsl() throws IOException {

        when(httpClientFactory.create(any())).thenReturn(closeableHttpClient);

        SslRequestConfig sslRequestConfig = SslRequestConfig.builder()
                .certPath(CERT_NAME)
                .build();
        ClientPoolRequestConfig clientPoolRequestConfig = ClientPoolRequestConfig.builder()
                .sslRequestConfig(sslRequestConfig)
                .build();
        CloseableHttpClient closeableHttpClient = httpClientPool.get(clientPoolRequestConfig);

        verify(httpClientFactory, timeout(1)).create(any());
        assertEquals(closeableHttpClient, closeableHttpClient);

        httpClientPool.get(clientPoolRequestConfig);
        httpClientPool.get(clientPoolRequestConfig);

        verify(httpClientFactory, timeout(1)).create(clientPoolRequestConfig);
        assertEquals(closeableHttpClient, closeableHttpClient);

        httpClientPool.destroy();

        verify(closeableHttpClient, times(1)).close();
    }

}

