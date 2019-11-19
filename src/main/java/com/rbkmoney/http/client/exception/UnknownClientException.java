package com.rbkmoney.http.client.exception;

public class UnknownClientException extends RuntimeException {
    public UnknownClientException() {
    }

    public UnknownClientException(String message) {
        super(message);
    }

    public UnknownClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownClientException(Throwable cause) {
        super(cause);
    }

    public UnknownClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
