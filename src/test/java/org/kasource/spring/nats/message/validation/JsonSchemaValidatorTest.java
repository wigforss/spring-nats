package org.kasource.spring.nats.message.validation;

import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.json.schema.registry.JsonSchemaRegistration;
import org.kasource.json.schema.registry.JsonSchemaRegistry;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JsonSchemaValidatorTest {

    @Mock
    private JsonSchemaRegistry registry;

    @Mock
    private JsonSchemaRegistration schemaRegistration;

    @Mock
    private org.kasource.json.schema.validation.impl.JsonSchemaValidator<String> stringJsonSchemaValidator;


    private JsonSchemaValidator validator;


    @Before
    public void setup() {
        validator = new JsonSchemaValidator(registry);
    }

    @Test
    public void nothingInRepository() {
        byte[] data = {};

        when(registry.getSchemaRegistration(Object.class)).thenReturn(Optional.empty());

        validator.validate(data, Object.class);
    }

    @Test
    public void validate() {
        byte[] data = {};

        when(registry.getSchemaRegistration(Object.class)).thenReturn(Optional.of(schemaRegistration));
        when(schemaRegistration.getStringValidator()).thenReturn(stringJsonSchemaValidator);

        validator.validate(data, Object.class);

        verify(stringJsonSchemaValidator).validate("");
    }



}
