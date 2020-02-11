package org.kasource.spring.nats.message.serde.protobuf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.util.ReflectionUtils;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;

public class ProtobufMessageDeserializer implements NatsMessageDeserializer {


    private Method method;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public ProtobufMessageDeserializer(final Class<? extends com.google.protobuf.Message> ofType) {
         method = ReflectionUtils.findMethod(ofType, "parseFrom", byte[].class);
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public Object fromMessage(Message message) throws DeserializeException {
        try {
            Object object = method.invoke(null, message.getData());
            validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
            return object;
        } catch (IllegalAccessException e) {
            throw new DeserializeException("Could not deserialize message " + message, e);
        } catch (InvocationTargetException e) {
            throw new DeserializeException("Could not deserialize message " + message, e.getCause()); // NOPMD
        }
    }
}
