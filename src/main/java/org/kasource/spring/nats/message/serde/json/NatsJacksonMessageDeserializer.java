package org.kasource.spring.nats.message.serde.json;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class NatsJacksonMessageDeserializer implements NatsMessageDeserializer {

    private ObjectReader objectReader;
    private Class<?> ofType;
    private Optional<JsonSchemaValidator> schemaValidator;
    private Optional<MessageObjectValidator> validator = Optional.empty();


    public NatsJacksonMessageDeserializer(final ObjectMapper objectMapper,
                                          final Class<?> ofType,
                                          final Optional<JsonSchemaValidator> schemaValidator) {
        this.objectReader = objectMapper.readerFor(ofType);
        this.ofType = ofType;
        this.schemaValidator = schemaValidator;
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public Object fromMessage(Message message) {
        try {
            String dataString = new String(message.getData(), StandardCharsets.UTF_8);
            schemaValidator.ifPresent((sv) -> sv.validate(message.getData(), ofType));
            Object object =  objectReader.readValue(dataString);

            validator.filter(v -> v.shouldValidate(ofType)).ifPresent((v) ->  v.validate(object));

            return object;
        } catch (IOException e) {

            throw new DeserializeException("Could not deserialize message from JSON to object", e);
        }

    }



}
