package org.kasource.spring.nats.message.validation;

import java.util.Collections;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validator;

import org.springframework.validation.annotation.Validated;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BeanValidationValidatorTest {

    @Mock
    private Validator validator;

    @Mock
    private ConstraintViolation<Object> violation;

    @Mock
    private Path propertyPath;

    @Mock
    private Object object;

    private BeanValidationValidator javaBeansValidator;

    @Before
    public void setup() {
        javaBeansValidator = new BeanValidationValidator(validator);
    }

    @Test
    public void shouldNotValidate() {
        assertThat(javaBeansValidator.shouldValidate(NoValidation.class), is(false));
    }

    @Test
    public void shouldValidate() {
        assertThat(javaBeansValidator.shouldValidate(DoValidation.class), is(true));
    }

    @Test
    public void validateOK() {
        when(validator.validate(object)).thenReturn(Collections.EMPTY_SET);

        javaBeansValidator.validate(object);
    }

    @Test(expected = ConstraintViolationException.class)
    public void validateNotOK() {
        Set<ConstraintViolation<Object>> violations = Set.of(violation);

        String message = "message";
        String invalidValue = "invalidValue";

        when(validator.validate(object)).thenReturn(violations);

        when(violation.getPropertyPath()).thenReturn(propertyPath);
        when(violation.getMessage()).thenReturn(message);
        when(violation.getInvalidValue()).thenReturn(invalidValue);

        try {
            javaBeansValidator.validate(object);
        } catch (ConstraintViolationException e) {
            assertThat(e.getMessage(), containsString(String.format(BeanValidationValidator.ERROR_MESSAGE, propertyPath, message, invalidValue)));
            throw e;
        }
    }

    private static class NoValidation {
    }

    @Validated
    private static class DoValidation {
    }
}
