package dev.vality.http.client.factory.configurer.async;

import dev.vality.http.client.properties.ClientPoolRequestConfig;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

public interface AsyncHttpClientConfigurer {

    void configure(HttpAsyncClientBuilder httpAsyncClientBuilder, ClientPoolRequestConfig commonConfig);

    boolean isApplicable(ClientPoolRequestConfig config);
}
