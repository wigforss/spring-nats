package org.kasource.spring.nats.consumer;


import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.springframework.context.SmartLifecycle;
import org.springframework.util.ReflectionUtils;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nats.client.Connection;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.metrics.NatsMetricsRegistry;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;


@RunWith(MockitoJUnitRunner.class)
public class NatsConsumerManagerImplTest {

    @Mock
    private Connection natsConnection;
    @Mock
    private NatsMessageSerDeFactory serDeFactory;

    @Mock
    private NatsMetricsRegistry natsMetricsRegistry;

    @Mock
    private Consumer consumer;

    @Mock
    private BiConsumer biConsumer;

    @Mock
    private Set<MessageConsumer> subscriptions;

    @Mock
    private MessageConsumer registration1;

    @Mock
    private MessageConsumer registration2;

    private long drainTimeoutSeconds = 2;


    private boolean autoStart = true;

    @Captor
    private ArgumentCaptor<MessageConsumer> messageDispatcherCaptor;
    private NatsConsumerManagerImpl manager;

    @Before
    public void setup() {
        manager = new NatsConsumerManagerImpl(natsConnection, serDeFactory, Optional.of(natsMetricsRegistry), drainTimeoutSeconds, autoStart);
        InjectionUtils.injectInto(subscriptions, manager, "subscriptions");
    }

    @Test
    public void registerConsumer() {
        String subject = "subject";

        SmartLifecycle response = manager.register(consumer, Object.class, subject);

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));

    }

    @Test
    public void registerConsumerWithQueue() {
        String subject = "subject";
        String queue = "queue";

        SmartLifecycle response = manager.register(consumer, Object.class, subject, queue);

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));

    }

    @Test
    public void registerBiConsumer() {
        String subject = "subject";

        SmartLifecycle response = manager.register(biConsumer, Object.class, subject);

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));
    }

    @Test
    public void registerBiConsumerWithQueue() {
        String subject = "subject";
        String queue = "queue";

        SmartLifecycle response = manager.register(biConsumer, Object.class, subject, queue);

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));
    }

    @Test
    public void registerMethod() {
        SmartLifecycle response = manager.register(new TestConsumer(), ReflectionUtils.findMethod(TestConsumer.class, "onObject", Object.class));

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void registerMethodWithNoAnnotation() {
        manager.register(new TestConsumer(), ReflectionUtils.findMethod(TestConsumer.class, "methodWithoutAnnotation", Object.class));

    }

    @Test
    public void registerMethodAndSubject() {
        String subject = "subject";

        SmartLifecycle response = manager.register(new TestConsumer(), ReflectionUtils.findMethod(TestConsumer.class, "onObject", Object.class), subject);

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));
    }

    @Test
    public void registerMethodSubjectAndQueue() {
        String subject = "subject";
        String queue = "queue";

        SmartLifecycle response = manager.register(new TestConsumer(), ReflectionUtils.findMethod(TestConsumer.class, "onObject", Object.class), subject, queue);

        verify(subscriptions, times(1)).add(messageDispatcherCaptor.capture());

        assertThat(messageDispatcherCaptor.getValue(), is(equalTo(response)));
    }

    @Test
    public void isAutoStart() {
        assertThat(manager.isAutoStartup(), is(true));
    }

    @Test
    public void start() {
        InjectionUtils.injectInto(Set.of(registration1, registration2), manager, "subscriptions");

        when(registration1.isAutoStartup()).thenReturn(true);
        when(registration2.isAutoStartup()).thenReturn(false);

        assertThat(manager.isRunning(), is(false));

        manager.start();

        verify(registration1, times(1)).start();
        verify(registration2, times(0)).start();

        assertThat(manager.isRunning(), is(true));

    }

    @Test
    public void stop() {

        InjectionUtils.injectInto(Set.of(registration1, registration2), manager, "subscriptions");

        when(registration1.isAutoStartup()).thenReturn(true);
        when(registration2.isAutoStartup()).thenReturn(false);

        assertThat(manager.isRunning(), is(false));
        manager.start();

        verify(registration1, times(1)).start();
        verify(registration2, times(0)).start();

        assertThat(manager.isRunning(), is(true));

        when(registration1.isRunning()).thenReturn(true);
        when(registration2.isRunning()).thenReturn(false);

        manager.stop();

        verify(registration1, times(1)).stop();
        verify(registration2, times(0)).stop();

        assertThat(manager.isRunning(), is(false));


    }

    static class TestConsumer {
        @org.kasource.spring.nats.annotation.Consumer(subject = "another-subject")
        public void onObject(Object object) {

        }

        public void methodWithoutAnnotation(Object object) {

        }
    }
}
