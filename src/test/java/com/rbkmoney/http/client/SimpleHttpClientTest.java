package com.rbkmoney.http.client;

import com.rbkmoney.http.client.domain.Response;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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

        Mockito.when(closeableHttpClient.execute(httpPost)).thenReturn(closeableHttpResponse);

        Response<Object> post = simpleHttpClient.post("test", httpPost, closeableHttpResponse -> {
            return null;
        }, closeableHttpClient);

        Assert.assertNull(post.getEntity());
    }

    @Test
    public void get() throws URISyntaxException, IOException {
        HttpGet httpGet = new HttpGet();
        httpGet.setURI(new URI("/test"));

        Mockito.when(closeableHttpClient.execute(httpGet)).thenReturn(closeableHttpResponse);

        Response<Object> post = simpleHttpClient.get("get-test", httpGet, closeableHttpResponse -> {
            return null;
        }, closeableHttpClient);

        Assert.assertNull(post.getEntity());
    }

}