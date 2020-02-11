package org.kasource.spring.nats.exception;

public class RequestTimeoutException extends RuntimeException {
    private static final long serialVersionUID = -9216066043132991863L;

    public RequestTimeoutException(final String message) {
        super(message);
    }

    public RequestTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
