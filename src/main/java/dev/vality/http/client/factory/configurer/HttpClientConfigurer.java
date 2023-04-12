package dev.vality.http.client.factory.configurer;

import dev.vality.http.client.properties.ClientPoolRequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;

public interface HttpClientConfigurer {

    void configure(HttpClientBuilder httpClientBuilder, ClientPoolRequestConfig commonConfig);

    boolean isApplicable(ClientPoolRequestConfig config);
}
