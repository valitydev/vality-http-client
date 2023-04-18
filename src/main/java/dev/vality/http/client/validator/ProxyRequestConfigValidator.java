package dev.vality.http.client.validator;

import dev.vality.http.client.properties.ProxyRequestConfig;

import java.util.Objects;

public class ProxyRequestConfigValidator implements RequestConfigValidator<ProxyRequestConfig> {
    @Override
    public void validate(ProxyRequestConfig config) {
        Objects.requireNonNull(config, "ProxyRequestConfig is null!");
        Objects.requireNonNull(config.getAddress(), "Proxy address is null!");
    }
}
