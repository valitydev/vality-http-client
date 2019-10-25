package com.rbkmoney.http.client;

import com.rbkmoney.http.client.domain.Response;
import com.rbkmoney.http.client.exception.RemoteInvocationException;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class SimpleHttpClientTest {

    @Mock
    CloseableHttpClient closeableHttpClient;
    @Mock
    CloseableHttpResponse closeableHttpResponse;

    private SimpleHttpClient simpleHttpClient = SimpleHttpClient.builder()
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

        when(closeableHttpClient.execute(httpPost)).thenReturn(closeableHttpResponse);

        Response<Object> post = simpleHttpClient.post("test", httpPost, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(post.getEntity());
    }

    @Test(expected = RemoteInvocationException.class)
    public void postException() throws URISyntaxException, IOException {
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(new URI("/test"));

        when(closeableHttpClient.execute(httpPost)).thenThrow(new RuntimeException("test!"));

        Response<Object> post = simpleHttpClient.post("test", httpPost, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(post.getEntity());
    }

    @Test
    public void get() throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(new URI("/test"));

        when(closeableHttpClient.execute(httpGet)).thenReturn(closeableHttpResponse);

        Response<Object> post = simpleHttpClient.get("get-test", httpGet, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(post.getEntity());
    }

    @Test
    public void delete() throws URISyntaxException, IOException {
        HttpDelete httpDelete = new HttpDelete();
        httpDelete.setURI(new URI("/test"));

        when(closeableHttpClient.execute(httpDelete)).thenReturn(closeableHttpResponse);

        Response<Object> deleteResponse = simpleHttpClient.delete("delete-test", httpDelete, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(deleteResponse.getEntity());
    }

    @Test
    public void put() throws URISyntaxException, IOException {
        HttpPut httpPut = new HttpPut();
        httpPut.setURI(new URI("/test"));

        when(closeableHttpClient.execute(httpPut)).thenReturn(closeableHttpResponse);

        Response<Object> deleteResponse = simpleHttpClient.put("put-test", httpPut, closeableHttpResponse -> null, closeableHttpClient);

        assertNull(deleteResponse.getEntity());
    }

}