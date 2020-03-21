package org.kasource.spring.nats.consumer;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.kasource.spring.nats.metrics.NatsMetricsRegistry;

import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class MessageConsumerTest {

    private String subject = "subject";
    private String queueName = "queueName";
    @Mock
    private MessageHandler messageHandler;
    @Mock
    private Connection natsConnection;
    private long drainTimeoutSeconds = 2000L;
    @Mock
    private NatsMetricsRegistry natsMetricsRegistry;
    private boolean autoStartup = true;

    @Mock
    private CompletableFuture<Boolean> drainResponse;

    @Mock
    private Dispatcher dispatcher;

    private MessageConsumer consumer;

    @Before
    public void setup() {
        consumer = new MessageConsumer(
                natsConnection,
                subject,
                queueName,
                messageHandler,
                drainTimeoutSeconds,
                natsMetricsRegistry,
                autoStartup);
    }

    @Test
    public void startNoDispatcher() {

        when(natsConnection.createDispatcher(messageHandler)).thenReturn(dispatcher);
        when(dispatcher.subscribe(subject, queueName)).thenReturn(dispatcher);

        consumer.start();

        verify(natsMetricsRegistry).registerMetrics(dispatcher, subject, queueName);
    }

    @Test
    public void startNotActive() {
        InjectionUtils.injectInto(dispatcher, consumer, "dispatcher");
        InjectionUtils.injectInto("", consumer, "queueName");

        when(dispatcher.isActive()).thenReturn(false);
        when(natsConnection.createDispatcher(messageHandler)).thenReturn(dispatcher);
        when(dispatcher.subscribe(subject)).thenReturn(dispatcher);

        consumer.start();

        verify(natsMetricsRegistry).registerMetrics(dispatcher, subject, "");
    }

    @Test
    public void startAlreadyActive() {
        InjectionUtils.injectInto(dispatcher, consumer, "dispatcher");

        when(dispatcher.isActive()).thenReturn(true);

        consumer.start();

        verifyZeroInteractions(natsConnection);
    }

    @Test
    public void toStringTest() {
        assertThat(consumer.toString(), stringContainsInOrder(List.of(
                "subject='" + subject + "'",
                "queueName='" + queueName + "'",
                "messageHandler=" + messageHandler)));
    }

    @Test
    public void stop() throws InterruptedException {
        InjectionUtils.injectInto(dispatcher, consumer, "dispatcher");

        when(dispatcher.drain(Duration.ofSeconds(drainTimeoutSeconds))).thenReturn(drainResponse);
        when(drainResponse.join()).thenReturn(true);

        consumer.stop();
    }
}
