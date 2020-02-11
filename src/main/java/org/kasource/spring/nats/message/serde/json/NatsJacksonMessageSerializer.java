package org.kasource.spring.nats.message.serde.json;


import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class NatsJacksonMessageSerializer implements NatsMessageSerializer {

    private ObjectWriter objectWriter;
    private Optional<JsonSchemaValidator> schemaValidator;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public NatsJacksonMessageSerializer(final ObjectMapper objectMapper,
                                        final Optional<JsonSchemaValidator> schemaValidator) {
        objectWriter = objectMapper.writer();
        this.schemaValidator = schemaValidator;
    }


    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public byte[] toMessageData(Object object) {
        try {
            validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
            byte[] data = objectWriter.writeValueAsBytes(object);
            schemaValidator.ifPresent((sv) -> sv.validate(data, object.getClass()));

            return data;
        } catch (JsonProcessingException e) {
            throw new SerializeException("Could not serialize " + object + " to JSON", e);
        }
    }

}
