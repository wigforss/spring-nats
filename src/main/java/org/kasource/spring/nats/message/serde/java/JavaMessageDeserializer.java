package org.kasource.spring.nats.message.serde.java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;

public class JavaMessageDeserializer implements NatsMessageDeserializer {

    private Optional<MessageObjectValidator> validator = Optional.empty();

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
       this.validator = validator;
    }

    @Override
    public Object fromMessage(Message message) throws DeserializeException {
        ByteArrayInputStream bis = new ByteArrayInputStream(message.getData());
        try (ObjectInputStream ois = new ObjectInputStream(bis)) {
            Object object = ois.readObject();
            validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
            return object;
        } catch (IOException | ClassNotFoundException e) {
            throw new DeserializeException("Could not deserialize message " + message, e);
        }
    }
}
