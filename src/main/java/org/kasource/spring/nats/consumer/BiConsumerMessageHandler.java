package org.kasource.spring.nats.consumer;

import java.util.function.BiConsumer;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;

import io.nats.client.Message;
import io.nats.client.MessageHandler;

public class BiConsumerMessageHandler implements MessageHandler {

    private BiConsumer consumer;
    private NatsMessageSerDeFactory serDeFactory;
    private Class<?> ofType;


    public <T> BiConsumerMessageHandler(final BiConsumer<T, ? super Message> consumer,
                                        final Class<T> ofType,
                                        final NatsMessageSerDeFactory serDeFactory) {
        this.consumer = consumer;
        this.serDeFactory = serDeFactory;
        this.ofType = ofType;
    }

    @Override
    public void onMessage(Message message) {
            NatsMessageDeserializer deserializer = serDeFactory.createDeserializer(ofType);
            consumer.accept(deserializer.fromMessage(message), message);
    }
}
