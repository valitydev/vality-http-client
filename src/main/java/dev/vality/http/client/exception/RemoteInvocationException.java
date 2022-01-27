package dev.vality.http.client.exception;

public class RemoteInvocationException extends RuntimeException {
    public RemoteInvocationException() {
    }

    public RemoteInvocationException(String message) {
        super(message);
    }

    public RemoteInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteInvocationException(Throwable cause) {
        super(cause);
    }

    public RemoteInvocationException(String message,
                                     Throwable cause,
                                     boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
