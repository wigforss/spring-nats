package org.kasource.spring.nats.message.validation;

import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;

import org.springframework.validation.annotation.Validated;

public class BeanValidationValidator implements MessageObjectValidator {
    static final String ERROR_MESSAGE = "%s %s was (%s)";
    private Validator validator;

    public BeanValidationValidator(final Validator validator) {
        this.validator = validator;
    }

    @Override
    public void validate(Object object) {
        Set<ConstraintViolation<Object>> invalid = validator.validate(object);
        if (!invalid.isEmpty()) {
            String constraintViolationMessage = invalid.stream()
                    .map(this::toErrorMessage)
                    .collect(Collectors.joining(","));
            throw new ConstraintViolationException("Invalid object: " + object + ": " + constraintViolationMessage, invalid);
        }
    }

    @Override
    public boolean shouldValidate(Class<?> clazz) {
        return clazz.isAnnotationPresent(Validated.class);
    }

    private String toErrorMessage(ConstraintViolation constraintViolation) {
        return String.format(ERROR_MESSAGE,
                constraintViolation.getPropertyPath(),
                constraintViolation.getMessage(),
                constraintViolation.getInvalidValue());


    }
}
