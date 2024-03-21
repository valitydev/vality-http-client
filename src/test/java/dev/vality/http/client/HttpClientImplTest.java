package dev.vality.http.client;

import dev.vality.http.client.domain.Response;
import dev.vality.http.client.exception.RemoteInvocationException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class HttpClientImplTest {

    @Mock
    CloseableHttpClient closeableHttpClient;
    @Mock
    CloseableHttpResponse closeableHttpResponse;

    private HttpClientImpl httpClientImpl = HttpClientImpl.builder()
            .registry(new SimpleMeterRegistry())
            .build();

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void post() throws URISyntaxException, IOException {
        HttpPost httpPost = new HttpPost(new URI("/test"));

        when(closeableHttpClient.execute(httpPost)).thenReturn(closeableHttpResponse);

        Response<Object> post =
                httpClientImpl.post("test", httpPost, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(post.getEntity());
    }

    @Test
    public void postException() throws URISyntaxException, IOException {
        HttpPost httpPost = new HttpPost(new URI("/test"));

        when(closeableHttpClient.execute(httpPost)).thenThrow(new RuntimeException("test!"));

        assertThrows(RemoteInvocationException.class,
                () -> httpClientImpl.post("test", httpPost, closeableHttpResponse -> null, closeableHttpClient));
    }

    @Test
    public void get() throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet(new URI("/test"));

        when(closeableHttpClient.execute(httpGet)).thenReturn(closeableHttpResponse);

        Response<Object> post =
                httpClientImpl.get("get-test", httpGet, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(post.getEntity());
    }

    @Test
    public void delete() throws URISyntaxException, IOException {
        HttpDelete httpDelete = new HttpDelete(new URI("/test"));

        when(closeableHttpClient.execute(httpDelete)).thenReturn(closeableHttpResponse);

        Response<Object> deleteResponse =
                httpClientImpl.delete("delete-test", httpDelete, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(deleteResponse.getEntity());
    }

    @Test
    public void put() throws URISyntaxException, IOException {
        HttpPut httpPut = new HttpPut(new URI("/test"));

        when(closeableHttpClient.execute(httpPut)).thenReturn(closeableHttpResponse);

        Response<Object> deleteResponse =
                httpClientImpl.put("put-test", httpPut, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(deleteResponse.getEntity());
    }

}