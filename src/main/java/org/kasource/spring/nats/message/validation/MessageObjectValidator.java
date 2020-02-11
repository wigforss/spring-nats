package org.kasource.spring.nats.message.validation;


public interface MessageObjectValidator {

    void validate(Object object);

    boolean shouldValidate(Class<?> clazz);
}
