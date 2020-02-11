package org.kasource.spring.nats.message.serde.json;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.google.gson.Gson;
import io.nats.client.Message;

public class NatsGsonMessageDeserializer implements NatsMessageDeserializer {


    private Gson gson;
    private Class<?> ofType;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public NatsGsonMessageDeserializer(final Gson gson,
                                       final Class<?> ofType) {
        this.gson = gson;
        this.ofType = ofType;
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public Object fromMessage(Message message) {
        String dataString = new String(message.getData(), StandardCharsets.UTF_8);
        Object object = gson.fromJson(dataString, ofType);
        validator.filter(v -> v.shouldValidate(ofType)).ifPresent(v -> v.validate(object));
        return object;
    }



}
