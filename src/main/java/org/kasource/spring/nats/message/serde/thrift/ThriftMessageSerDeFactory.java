package org.kasource.spring.nats.message.serde.thrift;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.InitializingBean;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import org.apache.thrift.TBase;

public class ThriftMessageSerDeFactory implements NatsMessageSerDeFactory, InitializingBean {

    private ThriftMessageSerializer serializer = new ThriftMessageSerializer();
    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        if (!TBase.class.isAssignableFrom(forClass)) {
            throw new IllegalStateException("Only thrift messages can be de-serialized " + forClass
                    + " is not a thrift message does not extend " + TBase.class.getName());
        }
        Optional<NatsMessageDeserializer> deserializer = Optional.ofNullable(deserializers.get(forClass));
        return deserializer.orElseGet(() -> createAndPut(forClass));
    }



    private NatsMessageDeserializer createAndPut(Class<?> forClass) {
        ThriftMessageDeserializer thriftDeserializer =
                new ThriftMessageDeserializer((Class<? extends TBase>) forClass);
        deserializers.put(forClass, thriftDeserializer);
        return thriftDeserializer;
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
    public void afterPropertiesSet() {
        serializer.setValidator(Optional.ofNullable(validator));
    }
}
