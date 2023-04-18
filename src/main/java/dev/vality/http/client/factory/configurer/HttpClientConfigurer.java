package dev.vality.http.client.factory.configurer;

import dev.vality.http.client.properties.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public interface HttpClientConfigurer {

    void configure(HttpClientBuilder httpClientBuilder, RequestConfig commonConfig);

    boolean isApplicable(RequestConfig config);
}
