package org.kasource.spring.nats.consumer;

import java.util.function.BiConsumer;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.nats.client.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BiConsumerMessageHandlerTest {

    @Mock
    private BiConsumer consumer;

    @Mock
    private NatsMessageSerDeFactory serDeFactory;

    @Mock
    private Message message;

    @Mock
    private NatsMessageDeserializer deserializer;

    private Class<?> ofType = String.class;

    private BiConsumerMessageHandler messageHandler;

    @Before
    public void setup() {
        messageHandler = new BiConsumerMessageHandler(consumer, ofType, serDeFactory);
    }

    @Test
    public void  onMessage() {
        String object = "object";

        when(serDeFactory.createDeserializer(ofType)).thenReturn(deserializer);
        when(deserializer.fromMessage(message)).thenReturn(object);
        messageHandler.onMessage(message);

        verify(consumer).accept(object, message);
    }


}
