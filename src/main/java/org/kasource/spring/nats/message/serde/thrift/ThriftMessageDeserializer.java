package org.kasource.spring.nats.message.serde.thrift;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

public class ThriftMessageDeserializer implements NatsMessageDeserializer {

    private Constructor<? extends TBase> constructor;
    private Optional<MessageObjectValidator> validator = Optional.empty();


    public ThriftMessageDeserializer(final Class<? extends TBase> ofType) {
        try {
            this.constructor = ofType.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("No empty constructor found for type " + ofType, e);
        }
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }


    @Override
    public Object fromMessage(Message message) throws DeserializeException {
        try {
            TBase object = constructor.newInstance();
            TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
            deserializer.deserialize(object, message.getData());
            validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
            return object;
        } catch (TException | InstantiationException | IllegalAccessException e) {
            throw new DeserializeException("Could not deserialize message " + message, e);
        } catch (InvocationTargetException e) {
            throw new DeserializeException("Could not deserialize message " + message, e.getCause()); // NOPMD
        }
    }
}
