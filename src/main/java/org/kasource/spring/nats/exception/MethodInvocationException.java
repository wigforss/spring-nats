package org.kasource.spring.nats.exception;

public class MethodInvocationException extends RuntimeException {
    private static final long serialVersionUID = -3569951574594064267L;

    public MethodInvocationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
