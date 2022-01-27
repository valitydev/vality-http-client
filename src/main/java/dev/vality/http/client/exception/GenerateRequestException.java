package dev.vality.http.client.exception;

public class GenerateRequestException extends RuntimeException {
    public GenerateRequestException() {
    }

    public GenerateRequestException(String message) {
        super(message);
    }

    public GenerateRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenerateRequestException(Throwable cause) {
        super(cause);
    }

    public GenerateRequestException(String message,
                                    Throwable cause,
                                    boolean enableSuppression,
                                    boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
