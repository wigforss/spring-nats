package org.kasource.spring.nats.message.serde.thrift;

import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

public class ThriftMessageSerializer implements NatsMessageSerializer {
    private Optional<MessageObjectValidator> validator = Optional.empty();

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public byte[] toMessageData(Object object) throws SerializeException {
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        try {
            return serializer.serialize((TBase) object);
        } catch (TException e) {
            throw new SerializeException("Could not serialize " + object, e);
        }
    }
}
