package org.kasource.spring.nats.message.serde.json;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.google.gson.Gson;

public class NatsGsonMessageSerializer implements NatsMessageSerializer {

    private Gson gson;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public NatsGsonMessageSerializer(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public byte[] toMessageData(Object object) {
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        return gson.toJson(object).getBytes(StandardCharsets.UTF_8);
    }
}
