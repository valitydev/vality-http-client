package com.rbkmoney.http.client.factory;

import com.rbkmoney.http.client.exception.GenerateRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class UrlParamsRequestFactory implements RequestFactory<Map<String, String>> {

    @Override
    public HttpPost create(Map<String, String> params, String url) {
        return createHttpPostUrlParams(params, url, DEFAULT_SOCKET_TIMEOUT);
    }

    @Override
    public HttpPost createHttpPostUrlParams(Map<String, String> request, String url, int executionTimeout) {
        return createHttpPostUrlParams(request, url, DEFAULT_CONNECTION_TIMEOUT, executionTimeout);
    }

    @Override
    public HttpPost createHttpPostUrlParams(Map<String, String> request, String url, int timeout, int executionTimeout) {
        HttpPost post = new HttpPost(url);
        post.setConfig(RequestConfig.custom()
                .setConnectTimeout(timeout)
                .setSocketTimeout(executionTimeout)
                .build());
        List<NameValuePair> urlParams = request.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParams, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("UrlParamsRequestFactory error when encode params e: ", e);
            throw new GenerateRequestException();
        }
        return post;
    }


}
