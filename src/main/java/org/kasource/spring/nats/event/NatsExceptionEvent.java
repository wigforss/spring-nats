package org.kasource.spring.nats.event;

import org.springframework.context.ApplicationEvent;

import io.nats.client.Connection;


public class NatsExceptionEvent extends ApplicationEvent {

    private static final long serialVersionUID = 3809359285392094970L;

    private Exception exception;
    /**
     * Create a new NatsExceptionEvent.
     *
     * @param connection the object on which the event initially occurred (never {@code null})
     */
    public NatsExceptionEvent(final Connection connection,
                              final Exception exception) {
        super(connection);
        this.exception = exception;
    }

    @Override
    public Connection getSource() {
        return (Connection) super.getSource();
    }

    public Exception getException() {
        return exception;
    }
}
