package dev.vality.http.client.factory.configurer.async;

import dev.vality.http.client.properties.RequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public interface AsyncHttpClientConfigurer {

    void configure(HttpAsyncClientBuilder httpAsyncClientBuilder, RequestConfig commonConfig);

    boolean isApplicable(RequestConfig config);
}
