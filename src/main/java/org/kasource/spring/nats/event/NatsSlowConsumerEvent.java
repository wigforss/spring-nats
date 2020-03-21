package org.kasource.spring.nats.event;

import org.springframework.context.ApplicationEvent;

import io.nats.client.Connection;
import io.nats.client.Consumer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


@SuppressFBWarnings("SE_TRANSIENT_FIELD_NOT_RESTORED")
public class NatsSlowConsumerEvent extends ApplicationEvent {

    private static final long serialVersionUID = 6122166868821152506L;

    private transient Consumer consumer;
    /**
     * Create a new NatsSlowConsumerEvent.
     *
     * @param connection the object on which the event initially occurred (never {@code null})
     */
    public NatsSlowConsumerEvent(final Connection connection,
                                 final Consumer consumer) {
        super(connection);
        this.consumer = consumer;
    }

    @Override
    public Connection getSource() {
        return (Connection) super.getSource();
    }

    public Consumer getConsumer() {
        return consumer;
    }
}
