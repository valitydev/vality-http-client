package com.rbkmoney.http.client.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.concurrent.FutureCallback;

@Slf4j
@RequiredArgsConstructor
public class LogFutureCallback implements FutureCallback<HttpResponse> {

    private final HttpRequestBase httpRequestBase;

    public void completed(final HttpResponse response2) {
        log.info(httpRequestBase.getRequestLine() + " -> " + response2.getStatusLine());
    }

    public void failed(final Exception ex) {
        log.error(httpRequestBase.getRequestLine() + " e: ", ex);
    }

    public void cancelled() {
        log.warn(httpRequestBase.getRequestLine() + " cancelled");
    }

}