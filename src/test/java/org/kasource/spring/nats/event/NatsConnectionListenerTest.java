package org.kasource.spring.nats.event;

import org.springframework.context.ApplicationEventPublisher;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsConnectionListenerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Connection natsConnection;

    @Captor
    private ArgumentCaptor<NatsConnectionEvent> eventCaptor;

    private NatsConnectionListener listener = new NatsConnectionListener();

    @Before
    public void setup() {
        listener.setApplicationEventPublisher(eventPublisher);
    }

    @Test
    public void connectionEvent() {
        ConnectionListener.Events state = ConnectionListener.Events.CONNECTED;

        listener.connectionEvent(natsConnection, state);

        verify(eventPublisher).publishEvent(eventCaptor.capture());

        assertThat(eventCaptor.getValue().getSource(), is(equalTo(natsConnection)));
        assertThat(eventCaptor.getValue().getState(), is(equalTo(state)));
    }

}
