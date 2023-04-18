package dev.vality.http.client;

import dev.vality.http.client.factory.HttpClientFactory;
import dev.vality.http.client.pool.DefaultHttpClientPool;
import dev.vality.http.client.pool.HttpClientPool;
import dev.vality.http.client.properties.RequestConfig;
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

public class DefaultHttpClientPoolTest {

    private static final String PROXY_KEY = "proxyKey";
    private static final String CERT_NAME = "certName";
    HttpClientPool<RequestConfig, CloseableHttpClient> httpClientPool;
    @Mock
    private HttpClientFactory httpClientFactory;
    @Mock
    private CloseableHttpClient closeableHttpClient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        httpClientPool =
                new DefaultHttpClientPool(httpClientFactory,
                        requestConfig -> {
                            String result = null;
                            if (requestConfig.getProxyRequestConfig() != null) {
                                result += requestConfig.getProxyRequestConfig();
                            }
                            if (requestConfig.getSslRequestConfig() != null) {
                                result += requestConfig.getSslRequestConfig().getCertFileInfo().getCertPath();
                            }
                            return result;
                        });
    }

    @Test
    public void getProxy() throws IOException {
        when(httpClientFactory.create(any())).thenReturn(closeableHttpClient);

        ProxyRequestConfig proxyRequestConfig = ProxyRequestConfig.builder()
                .build();
        RequestConfig requestConfig = RequestConfig.builder()
                .proxyRequestConfig(proxyRequestConfig)
                .build();
        CloseableHttpClient closeableHttpClientRes = httpClientPool.get(requestConfig);

        verify(httpClientFactory, timeout(1)).create(any());
        assertEquals(closeableHttpClient, closeableHttpClientRes);

        httpClientPool.get(requestConfig);
        httpClientPool.get(requestConfig);

        verify(httpClientFactory, timeout(1)).create(requestConfig);
        assertEquals(closeableHttpClient, closeableHttpClientRes);

        httpClientPool.destroy();

        verify(closeableHttpClientRes, times(1)).close();
    }

    @Test
    public void getSsl() throws IOException {

        when(httpClientFactory.create(any())).thenReturn(closeableHttpClient);

        SslRequestConfig sslRequestConfig = SslRequestConfig.builder()
                .certFileInfo(
                        SslRequestConfig.CertFileInfo.builder()
                                .certPath(CERT_NAME).build())
                .build();
        RequestConfig requestConfig = RequestConfig.builder()
                .sslRequestConfig(sslRequestConfig)
                .build();
        CloseableHttpClient closeableHttpClient = httpClientPool.get(requestConfig);

        verify(httpClientFactory, timeout(1)).create(any());
        assertEquals(closeableHttpClient, closeableHttpClient);

        httpClientPool.get(requestConfig);
        httpClientPool.get(requestConfig);

        verify(httpClientFactory, timeout(1)).create(requestConfig);
        assertEquals(closeableHttpClient, closeableHttpClient);

        httpClientPool.destroy();

        verify(closeableHttpClient, times(1)).close();
    }

}

