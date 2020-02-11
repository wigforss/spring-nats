package org.kasource.spring.nats.logging;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import org.kasource.spring.nats.event.NatsConnectionEvent;

import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionStatusLogger {
    @SuppressWarnings("PMD.LoggerIsNotStaticFinal") // For testing purposes not static final
    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    @Async
    @EventListener
    public void onConnectionEvent(NatsConnectionEvent event) {
        switch (event.getState()) {
            case CONNECTED:
            case RECONNECTED:
            case RESUBSCRIBED:
            case DISCOVERED_SERVERS:
                logger.info("Connection " + event.getSource().getConnectedUrl() + " state changed to " + event.getState().toString());
                break;
            default: // CLOSED and DISCONNECTED
                logger.warn("Connection " + event.getSource().getConnectedUrl() + " state changed to " + event.getState().toString());
                break;
        }
    }
}
