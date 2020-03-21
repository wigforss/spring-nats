package org.kasource.spring.nats.event;


import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import io.nats.client.Connection;
import io.nats.client.Consumer;
import io.nats.client.ErrorListener;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
                    justification = "Spring provides the eventPublisher")
public class NatsErrorListener implements ErrorListener, ApplicationEventPublisherAware {

    private ApplicationEventPublisher eventPublisher;

    @Override
    public void errorOccurred(Connection conn, String error) {
       eventPublisher.publishEvent(new NatsErrorEvent(conn, error));
    }

    @Override
    public void exceptionOccurred(Connection conn, Exception exception) {
        eventPublisher.publishEvent(new NatsExceptionEvent(conn, exception));
    }

    @Override
    public void slowConsumerDetected(Connection conn, Consumer consumer) {
        eventPublisher.publishEvent(new NatsSlowConsumerEvent(conn, consumer));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }
}
