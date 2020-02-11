package org.kasource.spring.nats.message.serde.java;

import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public class JavaMessageSerDeFactory implements NatsMessageSerDeFactory, InitializingBean {

    private JavaMessageDeserializer deserializer = new JavaMessageDeserializer();
    private JavaMessageSerializer serializer = new JavaMessageSerializer();
    private MessageObjectValidator validator;


    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        return deserializer;
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
        deserializer.setValidator(Optional.ofNullable(validator));
        serializer.setValidator(Optional.ofNullable(validator));
    }
}
