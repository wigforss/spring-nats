package org.kasource.spring.nats.exception;

public class SerializeException extends RuntimeException {

    private static final long serialVersionUID = -430557422499697012L;

    public SerializeException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
