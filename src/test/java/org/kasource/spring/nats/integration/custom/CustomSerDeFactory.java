package org.kasource.spring.nats.integration.custom;

import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import io.nats.client.Message;
import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public class CustomSerDeFactory implements NatsMessageSerDeFactory {

    @Override
    public NatsMessageDeserializer createDeserializer(final Class<?> forClass) {
        return new NatsMessageDeserializer() {
            @Override
            public void setValidator(Optional<MessageObjectValidator> validator) {

            }

            @Override
            public Object fromMessage(Message message) throws DeserializeException {
                try {
                    Constructor cons = forClass.getConstructor(String.class);
                    return cons.newInstance(new String(message.getData(), StandardCharsets.UTF_8));
                } catch (Exception e) {
                    throw new IllegalStateException("Could not create instance", e);
                }
            }
        };
    }

    @Override
    public NatsMessageSerializer createSerializer(){
        return new NatsMessageSerializer() {
            @Override
            public void setValidator(Optional<MessageObjectValidator> validator) {

            }

            @Override
            public byte[] toMessageData(Object object) throws SerializeException {
                return object.toString().getBytes(StandardCharsets.UTF_8);
            }
        };
    }

    @Override
    public void setValidator(MessageObjectValidator validator) {

    }
}
