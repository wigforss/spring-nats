package org.kasource.spring.nats.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class NatsConnectionListener implements ConnectionListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    @SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
                        justification = "Spring provides the eventPublisher")
    @Override
    public void connectionEvent(Connection connection, Events events) {
        eventPublisher.publishEvent(new NatsConnectionEvent(connection, events));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
