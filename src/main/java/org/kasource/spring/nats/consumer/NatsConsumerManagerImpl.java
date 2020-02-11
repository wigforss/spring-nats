package org.kasource.spring.nats.consumer;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.context.SmartLifecycle;

import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.metrics.NatsMetricsRegistry;

import io.nats.client.Connection;
import io.nats.client.Message;
import io.nats.client.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * NatsConsumerManager allows for binding lambda or annotated methods as NATS listeners.
 *
 * <pre>@code
 *
 *  @Autowired
 *  private NatsConsumerManager manager;
 *
 *  ...
 *  manager.register(s -> System.out.println(s), SomeType.class, "subject")
 *  ...
 * }</pre>
 *
 **/
public class NatsConsumerManagerImpl implements SmartLifecycle, NatsConsumerManager {


    private static final String REGISTERING_MESSAGE = "Registering dispatcher %s";
    private static final Logger LOG = LoggerFactory.getLogger(NatsConsumerManagerImpl.class);

    private Connection natsConnection;
    private NatsMessageSerDeFactory serDeFactory;
    private Optional<NatsMetricsRegistry> natsMetricsRegistry = Optional.empty();
    private Set<MessageConsumer> subscriptions = new CopyOnWriteArraySet<>();
    @SuppressWarnings("PMD.AvoidUsingVolatile")
    private volatile boolean running;
    private boolean autoStartup;
    private long drainTimeoutSeconds;

    public NatsConsumerManagerImpl(final Connection natsConnection,
                                   final NatsMessageSerDeFactory serDeFactory,
                                   final long drainTimeoutSeconds,
                                   final boolean autoStartup) {
        this.natsConnection = natsConnection;
        this.serDeFactory = serDeFactory;
        this.autoStartup = autoStartup;
        this.drainTimeoutSeconds = drainTimeoutSeconds;
    }

    public NatsConsumerManagerImpl(final Connection natsConnection,
                                   final NatsMessageSerDeFactory serDeFactory,
                                   final Optional<NatsMetricsRegistry> natsMetricsRegistry,
                                   final long drainTimeoutSeconds,
                                   final boolean autoStartup) {
        this.natsConnection = natsConnection;
        this.serDeFactory = serDeFactory;
        this.natsMetricsRegistry = natsMetricsRegistry;
        this.autoStartup = autoStartup;
        this.drainTimeoutSeconds = drainTimeoutSeconds;
    }

    public void setNatsMetricsRegistry(NatsMetricsRegistry natsMetricsRegistry) {
        this.natsMetricsRegistry = Optional.ofNullable(natsMetricsRegistry);
    }

    @Override
    public <T> SmartLifecycle register(Consumer<T> consumer, Class<T> ofType, String subject) {
        ConsumerMessageHandler messageHandler = new ConsumerMessageHandler(consumer, ofType, serDeFactory);
        return register(messageHandler, subject, "");
    }

    @Override
    public <T> SmartLifecycle register(Consumer<T> consumer, Class<T> ofType, String subject, String queueName) {

        ConsumerMessageHandler messageHandler = new ConsumerMessageHandler(consumer, ofType, serDeFactory);
        return register(messageHandler, subject, queueName);
    }

    @Override
    public <T> SmartLifecycle register(BiConsumer<T, ? super Message> consumer, Class<T> ofType, String subject) {
        BiConsumerMessageHandler messageHandler = new BiConsumerMessageHandler(consumer, ofType, serDeFactory);
        return register(messageHandler, subject, "");
    }

    @Override
    public <T> SmartLifecycle register(BiConsumer<T, ? super Message> consumer, Class<T> ofType, String subject, String queueName) {

        BiConsumerMessageHandler messageHandler = new BiConsumerMessageHandler(consumer, ofType, serDeFactory);
        return register(messageHandler, subject, queueName);
    }


    private SmartLifecycle register(MessageHandler messageHandler, String subject, String queueName) {

        MessageConsumer registration = new MessageConsumer(natsConnection,
                subject,
                queueName,
                messageHandler,
                drainTimeoutSeconds,
                natsMetricsRegistry.orElse(null),
                true);
        register(registration);

        return registration;
    }

    @Override
    public SmartLifecycle register(Object bean, Method method) {
        org.kasource.spring.nats.annotation.Consumer consumer = method.getAnnotation(org.kasource.spring.nats.annotation.Consumer.class);
        if (consumer == null) {
            throw new IllegalArgumentException("Method " + method + " must be annotated with @"
                    + org.kasource.spring.nats.annotation.Consumer.class.getName() + " in order to be registered as a NATS consumer");
        }
        return register(bean, method, consumer.subject(), consumer.queueName());
    }

    @Override
    public SmartLifecycle register(Object bean, Method method, String subject) {
        MethodMessageHandler messageHandler = new MethodMessageHandler(serDeFactory, bean, method);
        return register(messageHandler, subject, "");
    }

    @Override
    public SmartLifecycle register(Object bean, Method method, String subject, String queueName) {
        MethodMessageHandler messageHandler = new MethodMessageHandler(serDeFactory, bean, method);
        return register(messageHandler, subject, queueName);
    }

    @Override
    public void register(MessageConsumer messageConsumer) {

        subscriptions.add(messageConsumer);
        LOG.info(String.format(REGISTERING_MESSAGE, messageConsumer));

        if (isRunning() && messageConsumer.isAutoStartup()) {
            messageConsumer.start();
        }
    }


    @Override
    public boolean isAutoStartup() {
        return autoStartup;
    }

    @Override
    public void start() {
        subscriptions.stream()
                .filter(r -> r.isAutoStartup())
                .forEach(r -> r.start());
        running = true;
    }

    @Override
    public void stop() {
        subscriptions.stream()
                .filter(r -> r.isRunning())
                .forEach(r -> r.stop());
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

}
