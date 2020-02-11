package org.kasource.spring.nats.message.serde.avro;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public class AvroMessageSerDeFactory implements NatsMessageSerDeFactory, InitializingBean {
    private AvroMessageSerializer serializer = new AvroMessageSerializer();

    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        Optional<NatsMessageDeserializer> deserializer = Optional.ofNullable(deserializers.get(forClass));
        return deserializer.orElseGet(() -> createAndPut(forClass));
    }

    private NatsMessageDeserializer createAndPut(Class<?> forClass) {
        NatsMessageDeserializer avroDeserializer =
                    new AvroMessageDeserializer(forClass);

        avroDeserializer.setValidator(Optional.ofNullable(validator));
        deserializers.put(forClass, avroDeserializer);
        return avroDeserializer;
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
    public void afterPropertiesSet() throws Exception {
        serializer.setValidator(Optional.ofNullable(validator));
    }
}
