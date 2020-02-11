package org.kasource.spring.nats.event;

import org.springframework.context.ApplicationEvent;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;

public class NatsConnectionEvent extends ApplicationEvent {

    private static final long serialVersionUID = -2179570263334789281L;
    private ConnectionListener.Events state;

    /**
     * Create a new NatsConnectionEvent.
     *
     * @param connection the object on which the event initially occurred (never {@code null})
     */
    public NatsConnectionEvent(final Connection connection,
                               final ConnectionListener.Events state) {
        super(connection);
        this.state = state;
    }

    @Override
    public Connection getSource() {
        return (Connection) super.getSource();
    }

    public ConnectionListener.Events getState() {
        return state;
    }
}
