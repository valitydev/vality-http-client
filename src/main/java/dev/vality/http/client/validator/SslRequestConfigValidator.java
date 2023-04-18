package dev.vality.http.client.validator;

import dev.vality.http.client.exception.ClientConfigurationException;
import dev.vality.http.client.properties.SslRequestConfig;

import java.util.Objects;

public class SslRequestConfigValidator implements RequestConfigValidator<SslRequestConfig> {
    @Override
    public void validate(SslRequestConfig config) {
        Objects.requireNonNull(config, "SslRequestConfig is null!");
        if (Objects.isNull(config.getCertFileInfo()) == Objects.isNull(config.getCertPlainTextInfo())) {
            throw new ClientConfigurationException("Exactly one CertInfo configuration must be filled!");
        }
    }
}
