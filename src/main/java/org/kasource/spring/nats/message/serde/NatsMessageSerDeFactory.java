package org.kasource.spring.nats.message.serde;

import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public interface NatsMessageSerDeFactory {
    NatsMessageDeserializer createDeserializer(Class<?> forClass);

    NatsMessageSerializer createSerializer();

    void setValidator(MessageObjectValidator validator);
}
