package org.kasource.spring.nats.message.serde.protobuf;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.google.protobuf.Message;

public class ProtobufMessageSerDeFactory implements NatsMessageSerDeFactory, InitializingBean {
    private ProtobufMessageSerializer serializer = new ProtobufMessageSerializer();

    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        if (!Message.class.isAssignableFrom(forClass)) {
            throw new IllegalStateException("Only protobuf messages can be de-serialized " + forClass
                    + " is not a protobuf message does not extend " + Message.class.getName());
        }
        Optional<NatsMessageDeserializer> deserializer = Optional.ofNullable(deserializers.get(forClass));
        return deserializer.orElseGet(() -> createAndPut(forClass));
    }

    private NatsMessageDeserializer createAndPut(Class<?> forClass) {
        ProtobufMessageDeserializer protobufDeserializer =
                new ProtobufMessageDeserializer((Class<? extends Message>) forClass);
        protobufDeserializer.setValidator(Optional.ofNullable(validator));
        deserializers.put(forClass, protobufDeserializer);
        return protobufDeserializer;
    }

    @Override
    public NatsMessageSerializer createSerializer() {
        return serializer;
    }

    @Override
    public void setValidator(MessageObjectValidator validator) {
        this.validator = validator;
    }

    @Override
    public void afterPropertiesSet() {
        serializer.setValidator(Optional.ofNullable(validator));
    }
}
