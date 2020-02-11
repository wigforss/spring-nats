package org.kasource.spring.nats.exception;

public class RequestFailedException extends RuntimeException {

    private static final long serialVersionUID = 8141762352169291942L;

    public RequestFailedException(final String message) {
        super(message);
    }

    public RequestFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
