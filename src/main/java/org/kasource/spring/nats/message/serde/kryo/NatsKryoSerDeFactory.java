package org.kasource.spring.nats.message.serde.kryo;

import java.util.Optional;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public class NatsKryoSerDeFactory implements NatsMessageSerDeFactory {

    private KryoFactory kryoFactory = new DefaultKryoFactory();
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        NatsKryoMessageDeserializer deserializer = new NatsKryoMessageDeserializer(forClass, kryoFactory);
        deserializer.setValidator(Optional.ofNullable(validator));
        return deserializer;
    }

    @Override
    public NatsMessageSerializer createSerializer() {
        NatsKryoMessageSerializer serializer = new NatsKryoMessageSerializer(kryoFactory);
        serializer.setValidator(Optional.ofNullable(validator));
        return serializer;
    }

    public void setKryoFactory(KryoFactory kryoFactory) {
        this.kryoFactory = kryoFactory;
    }

    @Override
    public void setValidator(MessageObjectValidator validator) {
        this.validator = validator;
    }
}
