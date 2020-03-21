package org.kasource.spring.nats.metrics;

import java.util.function.ToDoubleFunction;

import static org.kasource.spring.nats.metrics.NatsMetricsRegistry.CONNECTION_PREFIX;
import static org.kasource.spring.nats.metrics.NatsMetricsRegistry.SUBSCRIPTION_PREFIX;
import org.kasource.spring.nats.event.NatsErrorEvent;
import org.kasource.spring.nats.event.NatsExceptionEvent;
import org.kasource.spring.nats.event.NatsSlowConsumerEvent;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Options;
import io.nats.client.Statistics;
import io.nats.client.Subscription;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsMetricsRegistryTest {

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Connection natsConnection;

    @Mock
    private Statistics statistics;

    @Mock
    private Options options;

    @Mock
    private Subscription subscription;

    @Mock
    private Dispatcher dispatcher;

    @Mock
    private NatsExceptionEvent natsExceptionEvent;

    @Mock
    private NatsErrorEvent natsErrorEvent;

    @Mock
    private NatsSlowConsumerEvent natsSlowConsumerEvent;

    @Mock
    private Counter counter;


    @Captor
    private ArgumentCaptor<Iterable<Tag>> tagsCaptor;

    @Captor
    private ArgumentCaptor<ToDoubleFunction> functionCaptor;

    private NatsMetricsRegistry registry;

    @Before
    public void setup() {
        registry = new NatsMetricsRegistry(meterRegistry, natsConnection);
    }

    @Test
    public void registerMetrics() {

        String url = "url";
        String connectionName = "connectionName";
        final Long droppedCount = 1L;
        final Long reconnects = 2L;
        final Long inMessageCount = 3L;
        final Long outMessageCount = 4L;
        final Long inBytesCount = 5L;
        final Long outBytesCount = 6L;
        final Integer status = Connection.Status.CONNECTED.ordinal();

        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(natsConnection.getOptions()).thenReturn(options);
        when(options.getConnectionName()).thenReturn(connectionName);


        when(natsConnection.getStatistics()).thenReturn(statistics);
        when(statistics.getDroppedCount()).thenReturn(droppedCount);
        when(statistics.getReconnects()).thenReturn(reconnects);
        when(statistics.getInMsgs()).thenReturn(inMessageCount);
        when(statistics.getOutMsgs()).thenReturn(outMessageCount);
        when(statistics.getInBytes()).thenReturn(inBytesCount);
        when(statistics.getOutBytes()).thenReturn(outBytesCount);
        when(natsConnection.getStatus()).thenReturn(Connection.Status.CONNECTED);

        registry.afterPropertiesSet();

        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "dropped.count"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "reconnect.count"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "in.message.count"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "out.message.count"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "in.bytes"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "out.bytes"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "status"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());

        verify(meterRegistry).counter(eq(CONNECTION_PREFIX + "exception.count"), tagsCaptor.capture());
        verify(meterRegistry).counter(eq(CONNECTION_PREFIX + "error.count"), tagsCaptor.capture());
        verify(meterRegistry).counter(eq(CONNECTION_PREFIX + "slowConsumer.count"), tagsCaptor.capture());

        // All tags should be the same
        Iterable<Tag> tags = tagsCaptor.getAllValues().get(0);
        assertThat(tags, containsInAnyOrder(Tag.of("url", url), Tag.of("connection-name", connectionName)));
        assertThat(tagsCaptor.getAllValues().stream().allMatch(t -> tags.equals(t)), is(true));


        assertThat(functionCaptor.getAllValues().get(0).applyAsDouble(natsConnection), is(droppedCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(1).applyAsDouble(natsConnection), is(reconnects.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(2).applyAsDouble(natsConnection), is(inMessageCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(3).applyAsDouble(natsConnection), is(outMessageCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(4).applyAsDouble(natsConnection), is(inBytesCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(5).applyAsDouble(natsConnection), is(outBytesCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(6).applyAsDouble(natsConnection), is(status.doubleValue()));

    }

    @Test
    public void registerMetricsNoConnectionName() {
        String url = "url";
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(natsConnection.getOptions()).thenReturn(options);

        registry.afterPropertiesSet();

        verify(meterRegistry).gauge(eq(CONNECTION_PREFIX + "dropped.count"), tagsCaptor.capture(), eq(natsConnection), functionCaptor.capture());

        Iterable<Tag> tags = tagsCaptor.getAllValues().get(0);
        assertThat(tags, contains(Tag.of("url", url)));
    }

    @Test
    public void createMetricsForSubscription() {
        String subject = "subject";
        String queueName = "queueName";
        final Long droppedCount = 1L;
        final Long deliveredCount = 2L;
        final Long pendingBytesCount = 3L;
        final Long pendingMessageCount = 4L;
        final Long pendingBytesLimit = 5L;
        final Long pendingMessagesLimit = 6L;
        final boolean active = true;


        when(subscription.getSubject()).thenReturn(subject);
        when(subscription.getQueueName()).thenReturn(queueName);
        when(subscription.getDroppedCount()).thenReturn(droppedCount);
        when(subscription.getDeliveredCount()).thenReturn(deliveredCount);
        when(subscription.getPendingByteCount()).thenReturn(pendingBytesCount);
        when(subscription.getPendingMessageCount()).thenReturn(pendingMessageCount);
        when(subscription.getPendingByteLimit()).thenReturn(pendingBytesLimit);
        when(subscription.getPendingMessageLimit()).thenReturn(pendingMessagesLimit);
        when(subscription.isActive()).thenReturn(active);

        registry.registerMetrics(subscription);

        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "dropped.count"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "delivered.count"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.byte.count"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.message.count"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.byte.limit"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.message.limit"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "active"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());

        // All tags should be the same
        Iterable<Tag> tags = tagsCaptor.getAllValues().get(0);
        assertThat(tags, containsInAnyOrder(Tag.of("subject", subject), Tag.of("queue-name", queueName)));
        assertThat(tagsCaptor.getAllValues().stream().allMatch(t -> tags.equals(t)), is(true));

        assertThat(functionCaptor.getAllValues().get(0).applyAsDouble(subscription), is(droppedCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(1).applyAsDouble(subscription), is(deliveredCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(2).applyAsDouble(subscription), is(pendingBytesCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(3).applyAsDouble(subscription), is(pendingMessageCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(4).applyAsDouble(subscription), is(pendingBytesLimit.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(5).applyAsDouble(subscription), is(pendingMessagesLimit.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(6).applyAsDouble(subscription), is(1D));


    }

    @Test
    public void createMetricsForSubscriptionNoQueueName() {
        String subject = "subject";

        when(subscription.getSubject()).thenReturn(subject);
        when(subscription.isActive()).thenReturn(false);

        registry.registerMetrics(subscription);

        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "active"), tagsCaptor.capture(), eq(subscription), functionCaptor.capture());

        assertThat(functionCaptor.getValue().applyAsDouble(subscription), is(0D));

        Iterable<Tag> tags = tagsCaptor.getAllValues().get(0);
        assertThat(tags, contains(Tag.of("subject", subject)));
    }

    @Test
    public void createMetricsForDispatcher() {
        String subject = "subject";
        String queueName = "queueName";
        final Long droppedCount = 1L;
        final Long deliveredCount = 2L;
        final Long pendingBytesCount = 3L;
        final Long pendingMessageCount = 4L;
        final Long pendingBytesLimit = 5L;
        final Long pendingMessagesLimit = 6L;
        final boolean active = true;

        when(dispatcher.getDroppedCount()).thenReturn(droppedCount);
        when(dispatcher.getDeliveredCount()).thenReturn(deliveredCount);
        when(dispatcher.getPendingByteCount()).thenReturn(pendingBytesCount);
        when(dispatcher.getPendingMessageCount()).thenReturn(pendingMessageCount);
        when(dispatcher.getPendingByteLimit()).thenReturn(pendingBytesLimit);
        when(dispatcher.getPendingMessageLimit()).thenReturn(pendingMessagesLimit);
        when(dispatcher.isActive()).thenReturn(active);

        registry.registerMetrics(dispatcher, subject, queueName);

        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "dropped.count"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "delivered.count"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.byte.count"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.message.count"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.byte.limit"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "pending.message.limit"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());
        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "active"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());

        // All tags should be the same
        Iterable<Tag> tags = tagsCaptor.getAllValues().get(0);
        assertThat(tags, containsInAnyOrder(Tag.of("subject", subject), Tag.of("queue-name", queueName)));
        assertThat(tagsCaptor.getAllValues().stream().allMatch(t -> tags.equals(t)), is(true));

        assertThat(functionCaptor.getAllValues().get(0).applyAsDouble(dispatcher), is(droppedCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(1).applyAsDouble(dispatcher), is(deliveredCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(2).applyAsDouble(dispatcher), is(pendingBytesCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(3).applyAsDouble(dispatcher), is(pendingMessageCount.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(4).applyAsDouble(dispatcher), is(pendingBytesLimit.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(5).applyAsDouble(dispatcher), is(pendingMessagesLimit.doubleValue()));
        assertThat(functionCaptor.getAllValues().get(6).applyAsDouble(dispatcher), is(1D));


    }

    @Test
    public void createMetricsForDispatcherNoQueueName() {
        String subject = "subject";

        when(dispatcher.isActive()).thenReturn(false);

        registry.registerMetrics(dispatcher, subject, "");

        verify(meterRegistry).gauge(eq(SUBSCRIPTION_PREFIX + "active"), tagsCaptor.capture(), eq(dispatcher), functionCaptor.capture());

        assertThat(functionCaptor.getValue().applyAsDouble(dispatcher), is(0D));

        Iterable<Tag> tags = tagsCaptor.getAllValues().get(0);
        assertThat(tags, contains(Tag.of("subject", subject)));
    }

    @Test
    public void onException() {
        String url = "url";
        NullPointerException rootCause = new NullPointerException("Test NP");
        IllegalStateException illegalStateException = new IllegalStateException("Test", rootCause);
        String connectionName = "connectionName";
        when(natsExceptionEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(natsConnection.getOptions()).thenReturn(options);
        when(options.getConnectionName()).thenReturn(connectionName);
        when(natsExceptionEvent.getException()).thenReturn(illegalStateException);
        when(meterRegistry.counter(eq(CONNECTION_PREFIX + "exception.count"), tagsCaptor.capture())).thenReturn(counter);

        registry.onException(natsExceptionEvent);

        verify(counter).increment();

        assertThat(tagsCaptor.getValue(), containsInAnyOrder(
                Tag.of("url", url),
                Tag.of("connection-name", connectionName),
                Tag.of("exception", IllegalStateException.class.getName()),
                Tag.of("root-cause", NullPointerException.class.getName()))
        );
    }

    @Test
    public void onError() {
        String url = "url";
        when(natsErrorEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(natsConnection.getOptions()).thenReturn(options);
        when(meterRegistry.counter(eq(CONNECTION_PREFIX + "error.count"), tagsCaptor.capture())).thenReturn(counter);

        registry.onError(natsErrorEvent);

        verify(counter).increment();

        assertThat(tagsCaptor.getValue(), contains(Tag.of("url", url)));
    }

    @Test
    public void onSlowConsumer() {
        String url = "url";
        when(natsSlowConsumerEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);
        when(natsConnection.getOptions()).thenReturn(options);
        when(meterRegistry.counter(eq(CONNECTION_PREFIX + "slowConsumer.count"), tagsCaptor.capture())).thenReturn(counter);

        registry.onSlowConsumer(natsSlowConsumerEvent);

        verify(counter).increment();

        assertThat(tagsCaptor.getValue(), contains(Tag.of("url", url)));
    }

}
