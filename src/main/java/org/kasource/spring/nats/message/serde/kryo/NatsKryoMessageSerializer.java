package org.kasource.spring.nats.message.serde.kryo;


import java.io.ByteArrayOutputStream;
import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

public class NatsKryoMessageSerializer implements NatsMessageSerializer {

    private KryoFactory kryoFactory;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public NatsKryoMessageSerializer(final KryoFactory kryoFactory) {
        this.kryoFactory = kryoFactory;
    }


    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public byte[] toMessageData(Object object) throws SerializeException {
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        try {
            Kryo kryo = kryoFactory.createFor(object.getClass());
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try (Output output = new Output(bos)) {
                kryo.writeObject(output, object);
            }
            return bos.toByteArray();
        } catch (RuntimeException e) {
            throw new SerializeException("Could not serialize object " + object, e);
        }
    }
}
