package org.kasource.spring.nats.message.validation;

public interface MessageDataValidator {
    void validate(byte[] data, Class<?> ofType);
}
