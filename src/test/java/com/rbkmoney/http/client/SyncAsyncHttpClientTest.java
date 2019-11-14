package com.rbkmoney.http.client;

import com.rbkmoney.http.client.domain.Response;
import com.rbkmoney.http.client.exception.RemoteInvocationException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SyncAsyncHttpClientTest {

    @Mock
    CloseableHttpAsyncClient closeableHttpAsyncClient;
    @Mock
    Future<HttpResponse> httpResponse;

    private SyncAsyncHttpClient syncAsyncHttpClient = SyncAsyncHttpClient.builder()
            .registry(new SimpleMeterRegistry())
            .build();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void post() throws URISyntaxException, IOException {
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(new URI("/test"));

        when(closeableHttpAsyncClient.execute(any(), any())).thenReturn(httpResponse);

        Response<Object> post = syncAsyncHttpClient.post("test", httpPost, closeableHttpResponse -> null, closeableHttpAsyncClient);

        assertNull(post.getEntity());
    }

    @Test(expected = RemoteInvocationException.class)
    public void postException() throws URISyntaxException, IOException {
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(new URI("/test"));

        when(closeableHttpAsyncClient.execute(any(), any())).thenThrow(new RuntimeException("test!"));

        Response<Object> post = syncAsyncHttpClient.post("test", httpPost, closeableHttpResponse -> null, closeableHttpAsyncClient);

        assertNull(post.getEntity());
    }

    @Test
    public void get() throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(new URI("/test"));

        when(closeableHttpAsyncClient.execute(any(), any())).thenReturn(httpResponse);

        Response<Object> post = syncAsyncHttpClient.get("get-test", httpGet, closeableHttpResponse -> null, closeableHttpAsyncClient);

        assertNull(post.getEntity());
    }

    @Test
    public void delete() throws URISyntaxException, IOException {
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setURI(new URI("/test"));

        when(closeableHttpAsyncClient.execute(any(), any())).thenReturn(httpResponse);

        Response<Object> deleteResponse = syncAsyncHttpClient.delete("delete-test", httpDelete, closeableHttpResponse -> null, closeableHttpAsyncClient);

        assertNull(deleteResponse.getEntity());
    }

    @Test
    public void put() throws URISyntaxException, IOException {
        HttpPut httpPut = new HttpPut();
        httpPut.setURI(new URI("/test"));

        when(closeableHttpAsyncClient.execute(any(), any())).thenReturn(httpResponse);

        Response<Object> deleteResponse = syncAsyncHttpClient.put("put-test", httpPut, closeableHttpResponse -> null, closeableHttpAsyncClient);

        assertNull(deleteResponse.getEntity());
    }

}