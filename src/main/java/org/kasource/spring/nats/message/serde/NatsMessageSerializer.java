package org.kasource.spring.nats.message.serde;

import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public interface NatsMessageSerializer {
    void setValidator(Optional<MessageObjectValidator> validator);

    byte[] toMessageData(Object object) throws SerializeException;
}
