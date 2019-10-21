package com.rbkmoney.http.client;

import com.rbkmoney.http.client.domain.Response;
import com.rbkmoney.http.client.exception.RemoteInvocationException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.function.Function;

@Slf4j
@Builder
public class HttpClient {

    public <T> Response<T> post(HttpPost httpPost, Function<CloseableHttpResponse, T> handler, CloseableHttpClient client) {
        try {
            try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
                T result = handler.apply(httpResponse);
                log.debug("HttpClient httpPost: {} with result: {}", httpPost, result);
                return new Response<>(result);
            }
        } catch (Exception e) {
            log.error("Error when startCardP2PTrans e: ", e);
            throw new RemoteInvocationException(e);
        }
    }

}
