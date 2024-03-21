package dev.vality.http.client.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;

@Slf4j
@RequiredArgsConstructor
public class LogFutureCallback implements FutureCallback<SimpleHttpResponse> {

    private final SimpleHttpRequest httpRequestBase;

    public void completed(final SimpleHttpResponse response2) {
        log.debug("{} -> {} ({})", httpRequestBase.getRequestUri(), response2.getReasonPhrase(), response2.getCode());
    }

    public void failed(final Exception ex) {
        log.error("{} e: ", httpRequestBase.getRequestUri(), ex);
    }

    public void cancelled() {
        log.warn("{} cancelled", httpRequestBase.getRequestUri());
    }

}