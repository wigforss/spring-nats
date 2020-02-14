package org.kasource.spring.nats.consumer;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.context.SmartLifecycle;

import io.nats.client.Message;

/**
 * Consumer Manager which registers NATS message listeners.
 */
public interface NatsConsumerManager {

    /**
     * Register a java.util.function.Consumer as NATS message listener.
     *
     * @param consumer  The consumer to use
     * @param ofType    The type of the message payload
     * @param subject   The NATS subject to listen to
     * @param <T>       The type of the message payload
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     */
    <T> SmartLifecycle register(Consumer<T> consumer, Class<T> ofType, String subject);

    /**
     * Register a java.util.function.Consumer as NATS message listener.
     *
     * @param consumer  The consumer to use
     * @param ofType    The type of the message payload
     * @param subject   The NATS subject to listen to
     * @param queueName The name of the consumer group to share messages with
     * @param <T>       The type of the message payload
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     */
    <T> SmartLifecycle register(Consumer<T> consumer, Class<T> ofType, String subject, String queueName);

    /**
     * Register a java.util.function.BiConsumer as NATS message listener.
     *
     * @param consumer  The consumer to use
     * @param ofType    The type of the message payload
     * @param subject   The NATS subject to listen to
     * @param <T>       The type of the message payload
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     */
    <T> SmartLifecycle register(BiConsumer<T, ? super Message> consumer, Class<T> ofType, String subject);

    /**
     * Register a java.util.function.BiConsumer as NATS message listener.
     *
     * @param consumer  The consumer to use
     * @param ofType    The type of the message payload
     * @param subject   The NATS subject to listen to
     * @param queueName The name of the consumer group to share messages with
     * @param <T>       The type of the message payload
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     */
    <T> SmartLifecycle register(BiConsumer<T, ? super Message> consumer, Class<T> ofType, String subject, String queueName);

    /**
     * Register a method of an object as a NATS message listener.
     * The method is expected to be annotated with org.kasource.spring.nats.annotation.Consumer.
     *
     * @param bean      Object which has the method
     * @param method    The method to invoke on incoming messages
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     *
     * @throws IllegalArgumentException if the method is annotated with org.kasource.spring.nats.annotation.Consumer
     */
    SmartLifecycle register(Object bean, Method method);

    /**
     * Register a method of an object as a NATS message listener.
     *
     * @param bean      Object which has the method
     * @param method    The method to invoke on incoming messages
     * @param subject   The NATS subject to listen to
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     */
    SmartLifecycle register(Object bean, Method method, String subject);

    /**
     * Register a method of an object as a NATS message listener.
     *
     * @param bean      Object which has the method
     * @param method    The method to invoke on incoming messages
     * @param subject   The NATS subject to listen to
     * @param queueName The name of the consumer group to share messages with
     *
     * @return A SmartLifecycle object, so that the listeners lifecycle can be handled.
     */
    SmartLifecycle register(Object bean, Method method, String subject, String queueName);

    /**
     * Register a message consumer.
     *
     * @param messageConsumer The message consumer to register
     */
    void register(MessageConsumer messageConsumer);
}
