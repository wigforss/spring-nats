package org.kasource.spring.nats.exception;

public class DeserializeException extends RuntimeException {
    private static final long serialVersionUID = -639909759678075005L;

    public DeserializeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
