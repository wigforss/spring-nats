package org.kasource.spring.nats.event;

import org.springframework.context.ApplicationEvent;

import io.nats.client.Connection;


public class NatsErrorEvent extends ApplicationEvent {
    private static final long serialVersionUID = 3416517247294625594L;

    private String error;
    /**
     * Create a new NatsErrorEvent.
     *
     * @param connection the object on which the event initially occurred (never {@code null})
     */
    public NatsErrorEvent(final Connection connection,
                          final String error) {
        super(connection);
        this.error = error;
    }

    @Override
    public Connection getSource() {
        return (Connection) super.getSource();
    }

    public String getError() {
        return error;
    }
}
