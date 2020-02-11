package org.kasource.spring.nats.logging;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.event.NatsConnectionEvent;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionStatusLoggerTest {

    @Mock
    private Logger logger;

    @Mock
    private Connection natsConnection;

    @Mock
    private NatsConnectionEvent connectionEvent;

    private ConnectionStatusLogger connectionStatusLogger = new ConnectionStatusLogger();

    @Test
    public void logClosed() {
        ConnectionListener.Events state = ConnectionListener.Events.CLOSED;
        String url = "url";
        InjectionUtils.injectInto(logger, connectionStatusLogger, "logger");

        when(connectionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(connectionEvent.getState()).thenReturn(state);


        connectionStatusLogger.onConnectionEvent(connectionEvent);


        verify(logger).warn("Connection " + url + " state changed to " + state.toString());
    }

    @Test
    public void logDisconnected() {
        ConnectionListener.Events state = ConnectionListener.Events.DISCONNECTED;
        String url = "url";
        InjectionUtils.injectInto(logger, connectionStatusLogger, "logger");

        when(connectionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(connectionEvent.getState()).thenReturn(state);


        connectionStatusLogger.onConnectionEvent(connectionEvent);


        verify(logger).warn("Connection " + url + " state changed to " + state.toString());
    }

    @Test
    public void logConnected() {
        ConnectionListener.Events state = ConnectionListener.Events.CONNECTED;
        String url = "url";
        InjectionUtils.injectInto(logger, connectionStatusLogger, "logger");

        when(connectionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(connectionEvent.getState()).thenReturn(state);


        connectionStatusLogger.onConnectionEvent(connectionEvent);


        verify(logger).info("Connection " + url + " state changed to " + state.toString());
    }

    @Test
    public void logReconnected() {
        ConnectionListener.Events state = ConnectionListener.Events.RECONNECTED;
        String url = "url";
        InjectionUtils.injectInto(logger, connectionStatusLogger, "logger");

        when(connectionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(connectionEvent.getState()).thenReturn(state);


        connectionStatusLogger.onConnectionEvent(connectionEvent);


        verify(logger).info("Connection " + url + " state changed to " + state.toString());
    }

    @Test
    public void logResubscribed() {
        ConnectionListener.Events state = ConnectionListener.Events.RESUBSCRIBED;
        String url = "url";
        InjectionUtils.injectInto(logger, connectionStatusLogger, "logger");

        when(connectionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(connectionEvent.getState()).thenReturn(state);


        connectionStatusLogger.onConnectionEvent(connectionEvent);


        verify(logger).info("Connection " + url + " state changed to " + state.toString());
    }

    @Test
    public void logDiscoveredServers() {
        ConnectionListener.Events state = ConnectionListener.Events.DISCOVERED_SERVERS;
        String url = "url";
        InjectionUtils.injectInto(logger, connectionStatusLogger, "logger");

        when(connectionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(connectionEvent.getState()).thenReturn(state);


        connectionStatusLogger.onConnectionEvent(connectionEvent);


        verify(logger).info("Connection " + url + " state changed to " + state.toString());
    }
}
