package org.kasource.spring.nats.message.validation;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.kasource.json.schema.registry.JsonSchemaRegistration;
import org.kasource.json.schema.registry.JsonSchemaRegistry;

public class JsonSchemaValidator implements MessageDataValidator {

    private JsonSchemaRegistry jsonSchemaRegistry;

    public JsonSchemaValidator(final JsonSchemaRegistry jsonSchemaRegistry) {
        this.jsonSchemaRegistry = jsonSchemaRegistry;
    }

    @Override
    public void validate(byte[] data, Class<?> ofType) {
        Optional<JsonSchemaRegistration> schemaRegistration = jsonSchemaRegistry.getSchemaRegistration(ofType);
        schemaRegistration.ifPresent((s) -> s.getStringValidator().validate(new String(data, StandardCharsets.UTF_8)));
    }
}
