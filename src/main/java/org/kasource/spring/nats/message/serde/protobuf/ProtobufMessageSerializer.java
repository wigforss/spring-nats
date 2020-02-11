package org.kasource.spring.nats.message.serde.protobuf;

import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.google.protobuf.Message;

public class ProtobufMessageSerializer implements NatsMessageSerializer {
    private Optional<MessageObjectValidator> validator = Optional.empty();

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public byte[] toMessageData(Object object) throws SerializeException {
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        return ((Message) object).toByteArray();
    }
}
