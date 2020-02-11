package org.kasource.spring.nats.consumer;


import java.time.Duration;

import org.springframework.context.SmartLifecycle;

import org.kasource.spring.nats.metrics.NatsMetricsRegistry;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.MessageHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MessageConsumer implements SmartLifecycle {
    private static final Logger LOG = LoggerFactory.getLogger(MessageConsumer.class);
    private static final String SUBSCRIBE_MESSAGE_QUEUE = "%s dispatcher %s as subscriber to '%s' and queue '%s'";
    private String subject;
    private String queueName;
    private MessageHandler messageHandler;
    private Dispatcher dispatcher;
    private Connection natsConnection;
    private long drainTimeoutSeconds;
    private NatsMetricsRegistry natsMetricsRegistry;
    private boolean autoStartup;

    public MessageConsumer(final Connection natsConnection,
                           final String subject,
                           final String queueName,
                           final MessageHandler messageHandler,
                           final long drainTimeoutSeconds,
                           final NatsMetricsRegistry natsMetricsRegistry,
                           final boolean autoStartup) {
        this.natsConnection = natsConnection;
        this.subject = subject;
        this.queueName = queueName;
        this.messageHandler = messageHandler;
        this.drainTimeoutSeconds = drainTimeoutSeconds;
        this.natsMetricsRegistry = natsMetricsRegistry;
        this.autoStartup = autoStartup;
    }

    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public void start() {
        LOG.info(String.format(SUBSCRIBE_MESSAGE_QUEUE, "Starting", messageHandler, subject, queueName));
        if (!isRunning()) {
            dispatcher = natsConnection.createDispatcher(messageHandler);
            if (natsMetricsRegistry != null) {
                natsMetricsRegistry.registerMetrics(dispatcher, subject, queueName);
            }
            subscribe();
        }

    }

    private void subscribe() {
        if (StringUtils.isEmpty(queueName)) {
            dispatcher = dispatcher.subscribe(subject);
        } else {
            dispatcher = dispatcher.subscribe(subject, queueName);
        }
    }

    @Override
    public void stop() {
        LOG.info(String.format(SUBSCRIBE_MESSAGE_QUEUE, "Stopping", messageHandler, subject, queueName));
        try {
            dispatcher.drain(Duration.ofSeconds(drainTimeoutSeconds)).join();
        } catch (InterruptedException | RuntimeException e) {
            LOG.debug("Draining of subscription " + subject + " failed", e);
        }
    }

    @Override
    public boolean isRunning() {
        return dispatcher != null && dispatcher.isActive();
    }

    @Override
    public String toString() {
        return "MessageDispatcher{"
                + "subject='" + subject + '\''
                + ", queueName='" + queueName + '\''
                + ", messageHandler=" + messageHandler
                + '}';
    }
}
