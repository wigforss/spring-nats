package org.kasource.spring.nats.metrics;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import org.kasource.spring.nats.event.NatsErrorEvent;
import org.kasource.spring.nats.event.NatsExceptionEvent;
import org.kasource.spring.nats.event.NatsSlowConsumerEvent;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Subscription;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;


public class NatsMetricsRegistry implements InitializingBean {
    static final String CONNECTION_PREFIX = "nats.connection.";
    static final String SUBSCRIPTION_PREFIX = "nats.subscription.";


    private MeterRegistry meterRegistry;
    private Connection natsConnection;

    public NatsMetricsRegistry(final MeterRegistry meterRegistry,
                               final Connection natsConnection) {
        this.meterRegistry = meterRegistry;
        this.natsConnection = natsConnection;
    }


    public void registerMetrics(final Connection connection) {
        Tags tags = Tags.of(Tag.of("url", connection.getConnectedUrl()));

        String connectionName = connection.getOptions().getConnectionName();
        if (connectionName != null) {
            tags = tags.and(Tag.of("connection-name", connectionName));
        }

        meterRegistry.gauge(CONNECTION_PREFIX + "dropped.count", tags, connection, c -> c.getStatistics().getDroppedCount());
        meterRegistry.gauge(CONNECTION_PREFIX + "reconnect.count", tags, connection, c -> c.getStatistics().getReconnects());
        meterRegistry.gauge(CONNECTION_PREFIX + "in.message.count", tags, connection, c -> c.getStatistics().getInMsgs());
        meterRegistry.gauge(CONNECTION_PREFIX + "out.message.count", tags, connection, c -> c.getStatistics().getOutMsgs());
        meterRegistry.gauge(CONNECTION_PREFIX + "in.bytes", tags, connection, c -> c.getStatistics().getInBytes());
        meterRegistry.gauge(CONNECTION_PREFIX + "out.bytes", tags, connection, c -> c.getStatistics().getOutBytes());
        meterRegistry.gauge(CONNECTION_PREFIX + "status", tags, connection, c -> c.getStatus().ordinal());
        meterRegistry.counter(CONNECTION_PREFIX + "exception.count", tags);
        meterRegistry.counter(CONNECTION_PREFIX + "error.count", tags);
        meterRegistry.counter(CONNECTION_PREFIX + "slowConsumer.count", tags);
    }

    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    public void registerMetrics(final Subscription subscription) {


        Tags tags = Tags.of(Tag.of("subject", subscription.getSubject()));
        String queueName = subscription.getQueueName();
        if (queueName != null) {
            tags = tags.and(Tag.of("queue-name", queueName));
        }

        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "dropped.count", tags, subscription, s -> s.getDroppedCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "delivered.count", tags, subscription, s -> s.getDeliveredCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.byte.count", tags, subscription, s -> s.getPendingByteCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.message.count", tags, subscription, s -> s.getPendingMessageCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.byte.limit", tags, subscription, s -> s.getPendingByteLimit());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.message.limit", tags, subscription, s -> s.getPendingMessageLimit());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "active", tags, subscription, s -> s.isActive() ? 1L : 0L);

    }

    @SuppressWarnings("checkstyle:avoidinlineconditionals")
    public void registerMetrics(final Dispatcher dispatcher, String subject, @Nullable String queueName) {


        Tags tags = Tags.of(Tag.of("subject", subject));

        if (!StringUtils.isEmpty(queueName)) {
            tags = tags.and(Tag.of("queue-name", queueName));
        }

        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "dropped.count", tags, dispatcher, d -> d.getDroppedCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "delivered.count", tags, dispatcher, d -> d.getDeliveredCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.byte.count", tags, dispatcher, d -> d.getPendingByteCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.message.count", tags, dispatcher, d -> d.getPendingMessageCount());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.byte.limit", tags, dispatcher, d -> d.getPendingByteLimit());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "pending.message.limit", tags, dispatcher, d -> d.getPendingMessageLimit());
        meterRegistry.gauge(SUBSCRIPTION_PREFIX + "active", tags, dispatcher, s -> s.isActive() ? 1L : 0L);
    }

    @Async
    @EventListener
    public void onException(NatsExceptionEvent event) {
        List<Tag> tags = getTagsForConnection(event.getSource());
        tags.add(Tag.of("exception", event.getException().getClass().getName()));

        Optional<Throwable> rootCause = Optional.ofNullable(ExceptionUtils.getRootCause(event.getException()));
        rootCause.ifPresent((rc) -> tags.add(Tag.of("root-cause", rc.getClass().getName())));

        meterRegistry.counter(CONNECTION_PREFIX + "exception.count", tags).increment();
    }


    private String getUrl(final Connection connection) {
        if (connection.equals(natsConnection)) {
            return natsConnection.getConnectedUrl();
        }
        return Optional.ofNullable(connection.getConnectedUrl())
                .orElseGet(() -> connection.getOptions().getServers().iterator().next().toString());

    }



    @Async
    @EventListener
    public void onError(NatsErrorEvent event) {

        meterRegistry.counter(CONNECTION_PREFIX + "error.count", getTagsForConnection(event.getSource())).increment();
    }

    @Async
    @EventListener
    public void onSlowConsumer(NatsSlowConsumerEvent event) {
        meterRegistry.counter(CONNECTION_PREFIX + "slowConsumer.count", getTagsForConnection(event.getSource())).increment();
    }

    private List<Tag> getTagsForConnection(Connection connection) {
        final List<Tag> tags = new ArrayList<>(
                List.of(Tag.of("url", getUrl(connection))));

        Optional<String> connectionName = Optional.ofNullable(connection.getOptions().getConnectionName());
        connectionName.ifPresent((cn) -> tags.add(Tag.of("connection-name", cn)));
        return tags;
    }

    @Override
    public void afterPropertiesSet() {
        registerMetrics(natsConnection);
    }
}
