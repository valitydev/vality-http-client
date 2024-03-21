package dev.vality.http.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.util.Timeout;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@WireMockTest(httpPort = 8089)
public class AsyncHttpClientImplTest {

    private AsyncHttpClient asyncHttpClient;

    FutureCallback<HttpResponse> futureCallback;

    private CountDownLatch latchOk;
    private CountDownLatch latchFail;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        futureCallback = new FutureCallback<>() {
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
                                .setConnectionRequestTimeout(Timeout.ofSeconds(1))
                                //.setSocketTimeout(1)
                                .build())
                        .build())
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

//    @Test
//    public void post() throws ExecutionException, InterruptedException, IOException {
//        latchOk = new CountDownLatch(1);
//        SimpleHttpRequest httpPost =
//                SimpleHttpRequest.create(Method.POST, URI.create("http://127.0.0.1:8089/post/test"));
//        Future<SimpleHttpResponse> post = asyncHttpClient.execute("test", httpPost, futureCallback);
//        latchOk.await();
//        assertEquals(0, latchOk.getCount());
//        assertEquals("post", post.get().getBody().getBodyText());
//    }
//
//    @Test
//    public void postException() throws InterruptedException {
//        latchFail = new CountDownLatch(1);
//        SimpleHttpRequest httpPost =
//                SimpleHttpRequest.create(Method.POST, URI.create("http://127.0.0.1:8089/post/fail"));
//        Future<SimpleHttpResponse> post = asyncHttpClient.execute("test", httpPost, futureCallback);
//        latchFail.await();
//        assertEquals(0, latchFail.getCount());
//    }
//
//    @Test
//    public void get() throws ExecutionException, InterruptedException, IOException {
//        latchOk = new CountDownLatch(1);
//        SimpleHttpRequest httpGet =
//                SimpleHttpRequest.create(Method.GET, URI.create("http://127.0.0.1:8089/get/test"));
//        Future<SimpleHttpResponse> get = asyncHttpClient.execute("get-test", httpGet, futureCallback);
//        latchOk.await();
//        assertEquals(0, latchOk.getCount());
//        assertEquals("get", get.get().getBodyText());
//    }
//
//    @Test
//    public void delete() throws ExecutionException, InterruptedException, IOException {
//        latchOk = new CountDownLatch(1);
//        SimpleHttpRequest httpDelete =
//                SimpleHttpRequest.create(Method.DELETE, URI.create("http://127.0.0.1:8089/delete/test"));
//        Future<SimpleHttpResponse> deleteResponse =
//                asyncHttpClient.execute("delete-test", httpDelete, futureCallback);
//        latchOk.await();
//        assertEquals(0, latchOk.getCount());
//        assertEquals("delete", deleteResponse.get().getBodyText());
//    }
//
//    @Test
//    public void put() throws URISyntaxException, ExecutionException, InterruptedException, IOException {
//        latchOk = new CountDownLatch(1);
//        SimpleHttpRequest httpPut =
//                SimpleHttpRequest.create(Method.PUT, URI.create("http://127.0.0.1:8089/put/test"));
//        Future<SimpleHttpResponse> putResponse = asyncHttpClient.execute("put-test", httpPut, futureCallback);
//        latchOk.await();
//        assertEquals(0, latchOk.getCount());
//        assertEquals("put", putResponse.get().getBodyText());
//    }
}
