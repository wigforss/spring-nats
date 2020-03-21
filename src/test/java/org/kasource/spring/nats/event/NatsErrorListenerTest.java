package org.kasource.spring.nats.event;


import org.springframework.context.ApplicationEventPublisher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import io.nats.client.Connection;
import io.nats.client.Consumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class NatsErrorListenerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Connection natsConnection;

    @Mock
    private Consumer consumer;

    @Captor
    private ArgumentCaptor<NatsErrorEvent> errorEventCaptor;

    @Captor
    private ArgumentCaptor<NatsExceptionEvent> exceptionEventCaptor;

    @Captor
    private ArgumentCaptor<NatsSlowConsumerEvent> slowConsumerEventCaptor;

    @Mock
    private RuntimeException runtimeException;

    private NatsErrorListener listener = new NatsErrorListener();

    @Before
    public void setup() {
        listener.setApplicationEventPublisher(eventPublisher);

    }

    @Test
    public void errorOccurred() {
        String errorMessage = "errorMessage";

        listener.errorOccurred(natsConnection, errorMessage);

        verify(eventPublisher).publishEvent(errorEventCaptor.capture());

        assertThat(errorEventCaptor.getValue().getSource(), is(equalTo(natsConnection)));
        assertThat(errorEventCaptor.getValue().getError(), is(equalTo(errorMessage)));
    }

    @Test
    public void exceptionOccurred() {
        listener.exceptionOccurred(natsConnection, runtimeException);

        verify(eventPublisher).publishEvent(exceptionEventCaptor.capture());

        assertThat(exceptionEventCaptor.getValue().getSource(), is(equalTo(natsConnection)));
        assertThat(exceptionEventCaptor.getValue().getException(), is(equalTo(runtimeException)));
    }

    @Test
    public void slowConsumerDetected() {
        listener.slowConsumerDetected(natsConnection, consumer);

        verify(eventPublisher).publishEvent(slowConsumerEventCaptor.capture());

        assertThat(slowConsumerEventCaptor.getValue().getSource(), is(equalTo(natsConnection)));
        assertThat(slowConsumerEventCaptor.getValue().getConsumer(), is(equalTo(consumer)));

    }
}
