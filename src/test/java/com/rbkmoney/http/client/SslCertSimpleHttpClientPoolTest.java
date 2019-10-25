package com.rbkmoney.http.client;

import com.rbkmoney.http.client.factory.HttpClientFactory;
import com.rbkmoney.http.client.pool.HttpClientPool;
import com.rbkmoney.http.client.pool.SslCertHttpClientPool;
import com.rbkmoney.http.client.properties.SslRequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class SslCertSimpleHttpClientPoolTest {

    private static final String CERT_NAME = "certName";

    @Mock
    private HttpClientFactory httpClientFactory;
    @Mock
    private CloseableHttpClient closeableHttpClient;

    HttpClientPool<SslRequestConfig> httpClientPool;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        httpClientPool = new SslCertHttpClientPool(httpClientFactory, SslRequestConfig::getCertFileName);
    }

    @Test
    public void get() throws IOException {

        when(httpClientFactory.create(any())).thenReturn(closeableHttpClient);

        SslRequestConfig sslRequestConfig = SslRequestConfig.builder()
                .certFileName(CERT_NAME)
                .build();
        CloseableHttpClient closeableHttpClient = httpClientPool.get(sslRequestConfig);

        verify(httpClientFactory, timeout(1)).create(any());
        assertEquals(closeableHttpClient, closeableHttpClient);

        httpClientPool.get(sslRequestConfig);
        httpClientPool.get(sslRequestConfig);

        verify(httpClientFactory, timeout(1)).create(sslRequestConfig);
        assertEquals(closeableHttpClient, closeableHttpClient);

        httpClientPool.destroy();

        verify(closeableHttpClient, times(1)).close();
    }

}

