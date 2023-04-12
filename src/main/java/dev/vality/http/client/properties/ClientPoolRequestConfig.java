package dev.vality.http.client.properties;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ClientPoolRequestConfig {
    private ProxyRequestConfig proxyRequestConfig;
    private SslRequestConfig sslRequestConfig;
    private Map<String, String> additionalInfo;
}
