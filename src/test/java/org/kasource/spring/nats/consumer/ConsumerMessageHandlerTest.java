package org.kasource.spring.nats.consumer;

import java.util.function.Consumer;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import io.nats.client.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConsumerMessageHandlerTest {

    @Mock
    private Consumer consumer;

    @Mock
    private NatsMessageSerDeFactory serDeFactory;

    @Mock
    private Message message;

    @Mock
    private NatsMessageDeserializer deserializer;

    private Class<?> ofType = String.class;

    private ConsumerMessageHandler messageHandler;

    @Before
    public void setup() {
        messageHandler = new ConsumerMessageHandler(consumer, ofType, serDeFactory);
    }

    @Test
    public void  onMessage() {
        String object = "object";

        when(serDeFactory.createDeserializer(ofType)).thenReturn(deserializer);
        when(deserializer.fromMessage(message)).thenReturn(object);
        messageHandler.onMessage(message);

        verify(consumer).accept(object);
    }

    @Test
    public void  onMessageWithMessage() {
        messageHandler = new ConsumerMessageHandler(consumer, Message.class, serDeFactory);

        messageHandler.onMessage(message);

        verifyZeroInteractions(serDeFactory);

        verify(consumer).accept(message);
    }


}
