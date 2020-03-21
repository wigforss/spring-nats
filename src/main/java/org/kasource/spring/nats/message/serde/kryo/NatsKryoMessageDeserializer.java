package org.kasource.spring.nats.message.serde.kryo;

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

public class NatsKryoMessageDeserializer implements NatsMessageDeserializer {

    private Class<?> ofType;
    private KryoFactory kryoFactory;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public NatsKryoMessageDeserializer(final Class<?> ofType, final KryoFactory kryoFactory) {
        this.ofType = ofType;
        this.kryoFactory = kryoFactory;
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public Object fromMessage(Message message) throws DeserializeException {
        Object object;
        try {
            Kryo kryo = kryoFactory.createFor(ofType);

            object = kryo.readObject(new Input(new ByteArrayInputStream(message.getData())), ofType);

        } catch (RuntimeException e) {
            throw new DeserializeException("Could not de-serialize message " + message, e);
        }
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        return object;
    }
}
