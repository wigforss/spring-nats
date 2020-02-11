package org.kasource.spring.nats.consumer;

import java.util.function.Consumer;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;

import io.nats.client.Message;
import io.nats.client.MessageHandler;

public class ConsumerMessageHandler implements MessageHandler {

    private Consumer consumer;
    private NatsMessageSerDeFactory serDeFactory;
    private Class<?> ofType;
    private boolean doSerialize;

    public <T> ConsumerMessageHandler(final Consumer<T> consumer,
                                      final Class<T> ofType,
                                      final NatsMessageSerDeFactory serDeFactory) {
        this.consumer = consumer;
        this.serDeFactory = serDeFactory;
        this.ofType = ofType;
        this.doSerialize = !Message.class.isAssignableFrom(ofType);

    }

    @Override
    public void onMessage(Message message) {
        if (doSerialize) {
            NatsMessageDeserializer deserializer = serDeFactory.createDeserializer(ofType);
            consumer.accept(deserializer.fromMessage(message));
        } else {
            consumer.accept(message);
        }

    }
}
