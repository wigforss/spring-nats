package org.kasource.spring.nats.message.serde;

import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;


public interface NatsMessageDeserializer {

    void setValidator(Optional<MessageObjectValidator> validator);

    Object fromMessage(Message message) throws DeserializeException;
}
