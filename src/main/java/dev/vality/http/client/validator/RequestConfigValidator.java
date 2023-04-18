package dev.vality.http.client.validator;

public interface RequestConfigValidator<T> {

    void validate(T config);
}
