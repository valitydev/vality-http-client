package dev.vality.http.client;

import dev.vality.http.client.factory.HttpClientFactory;
import dev.vality.http.client.pool.SslCertHttpClientPool;
import dev.vality.http.client.properties.SslRequestConfig;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.*;

public class SslCertSimpleHttpClientPoolTest {

    private static final String CERT_NAME = "certName";

    @Mock
    private HttpClientFactory httpClientFactory;
    @Mock
    private CloseableHttpClient closeableHttpClient;

    HttpClientPool<SslRequestConfig, CloseableHttpClient> httpClientPool;

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

