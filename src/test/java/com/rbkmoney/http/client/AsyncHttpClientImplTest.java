package com.rbkmoney.http.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.util.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;


public class AsyncHttpClientImplTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8089);

    private AsyncHttpClient asyncHttpClient;

    FutureCallback<HttpResponse> futureCallback;

    private CountDownLatch latchOk;
    private CountDownLatch latchFail;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        futureCallback = new FutureCallback<HttpResponse>() {
            @Override
            public void completed(HttpResponse httpResponse) {
                latchOk.countDown();
            }

            @Override
            public void failed(Exception e) {
                latchFail.countDown();
            }

            @Override
            public void cancelled() {
            }
        };

        asyncHttpClient = AsyncHttpClientImpl.builder()
                .registry(new SimpleMeterRegistry())
                .client(HttpAsyncClients.custom()
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setConnectionRequestTimeout(1)
                                .setSocketTimeout(1)
                                .build())
                        .build())
                .callback(futureCallback)
                .build();

        stubFor(WireMock.post(urlEqualTo("/post/test"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("post")));

        stubFor(WireMock.post(urlEqualTo("/post/fail"))
                .willReturn(aResponse()
                        .withFault(Fault.MALFORMED_RESPONSE_CHUNK)));

        stubFor(WireMock.get(urlEqualTo("/get/test"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("get")));

        stubFor(WireMock.delete(urlEqualTo("/delete/test"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("delete")));


        stubFor(WireMock.put(urlEqualTo("/put/test"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("put")));
    }

    @Test
    public void post() throws ExecutionException, InterruptedException, IOException {
        latchOk = new CountDownLatch(1);
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8089/post/test");
        Future<HttpResponse> post = asyncHttpClient.post("test", httpPost);
        latchOk.await();
        assertEquals(0, latchOk.getCount());
        assertEquals("post", IOUtils.toString(post.get().getEntity().getContent()));
    }

    @Test
    public void postException() throws InterruptedException {
        latchFail = new CountDownLatch(1);
        HttpPost httpPost = new HttpPost("http://127.0.0.1:8089/post/fail");
        Future<HttpResponse> post = asyncHttpClient.post("test", httpPost);
        latchFail.await();
        assertEquals(0, latchFail.getCount());
    }

    @Test
    public void get() throws ExecutionException, InterruptedException, IOException {
        latchOk = new CountDownLatch(2);
        HttpGet httpGet = new HttpGet("http://127.0.0.1:8089/get/test");
        Future<HttpResponse> get = asyncHttpClient.get("get-test", httpGet);
        Thread.sleep(100);
        assertEquals(1, latchOk.getCount());
        assertNotNull(get.get().getEntity());
        get = asyncHttpClient.get("get-test", httpGet);
        latchOk.await();
        assertEquals(0, latchOk.getCount());
        assertEquals("get", IOUtils.toString(get.get().getEntity().getContent()));
    }

    @Test
    public void delete() throws ExecutionException, InterruptedException, IOException {
        latchOk = new CountDownLatch(2);
        HttpDelete httpDelete = new HttpDelete("http://127.0.0.1:8089/delete/test");
        Future<HttpResponse> deleteResponse = asyncHttpClient.delete("delete-test", httpDelete);
        Thread.sleep(100);
        assertEquals(1, latchOk.getCount());
        assertNotNull(deleteResponse.get().getEntity());
        deleteResponse = asyncHttpClient.delete("delete-test", httpDelete);
        latchOk.await();
        assertEquals(0, latchOk.getCount());
        assertEquals("delete", IOUtils.toString(deleteResponse.get().getEntity().getContent()));
    }

    @Test
    public void put() throws URISyntaxException, ExecutionException, InterruptedException, IOException {
        latchOk = new CountDownLatch(2);
        HttpPut httpPut = new HttpPut();
        httpPut.setURI(new URI("http://127.0.0.1:8089/put/test"));
        Future<HttpResponse> putResponse = asyncHttpClient.put("put-test", httpPut);
        Thread.sleep(100);
        assertEquals(1, latchOk.getCount());
        assertNotNull(putResponse.get().getEntity());
        putResponse = asyncHttpClient.put("put-test", httpPut);
        latchOk.await();
        assertEquals(0, latchOk.getCount());
        assertEquals("put", IOUtils.toString(putResponse.get().getEntity().getContent()));
    }
}
