package org.kasource.spring.nats.consumer;

import java.lang.reflect.Method;
import java.util.Optional;

import org.springframework.util.ReflectionUtils;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import io.nats.client.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.MethodInvocationException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class MethodMessageHandlerTest {


    private TestConsumer bean = new TestConsumer();
    private Method method = ReflectionUtils.findMethod(TestConsumer.class, "onObject", Object.class);

    @Mock
    private NatsMessageSerDeFactory serDeFactory;
    @Mock
    private NatsMessageDeserializer deserializer;

    @Mock
    private Message message;



    @Test
    public void onMessageInvocation() {
        when(serDeFactory.createDeserializer(Object.class)).thenReturn(deserializer);
        MethodMessageHandler messageHandler = new MethodMessageHandler(serDeFactory, bean, method);
        String object = "object";

        when(deserializer.fromMessage(message)).thenReturn(object);

        messageHandler.onMessage(message);


        assertThat(bean.getReceivedObject().isPresent(), is(true));
        assertThat(bean.getReceivedObject().get(), is(equalTo(object)));
        assertThat(bean.getReceivedMessage().isPresent(), is(false));
    }

    @Test
    public void onMessageInvocationAsMessage() {
        MethodMessageHandler messageHandler = new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "onMessage", Message.class));

        messageHandler.onMessage(message);

        verifyZeroInteractions(deserializer);

        assertThat(bean.getReceivedMessage().isPresent(), is(true));
        assertThat(bean.getReceivedMessage().get(), is(equalTo(message)));
        assertThat(bean.getReceivedObject().isPresent(), is(false));
    }




    @Test
    public void onMessageAsObjectAndMessageInvocation() {
        when(serDeFactory.createDeserializer(Object.class)).thenReturn(deserializer);
        MethodMessageHandler methodConsumer = new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "onObjectAndMessage", Object.class, Message.class));
        String object = "object";

        when(deserializer.fromMessage(message)).thenReturn(object);

        methodConsumer.onMessage(message);


        assertThat(bean.getReceivedObject().isPresent(), is(true));
        assertThat(bean.getReceivedObject().get(), is(equalTo(object)));

        assertThat(bean.getReceivedMessage().isPresent(), is(true));
        assertThat(bean.getReceivedMessage().get(), is(equalTo(message)));
    }

    @Test
    public void onMessageAsMessageAndObjectInvocation() {
        when(serDeFactory.createDeserializer(Object.class)).thenReturn(deserializer);

        MethodMessageHandler methodConsumer = new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "onMessageAndObject", Message.class, Object.class));
        String object = "object";

        when(deserializer.fromMessage(message)).thenReturn(object);

        methodConsumer.onMessage(message);


        assertThat(bean.getReceivedObject().isPresent(), is(true));
        assertThat(bean.getReceivedObject().get(), is(equalTo(object)));

        assertThat(bean.getReceivedMessage().isPresent(), is(true));
        assertThat(bean.getReceivedMessage().get(), is(equalTo(message)));
    }

    @Test(expected = MethodInvocationException.class)
    public void onMessageNullArgument() {
        when(serDeFactory.createDeserializer(Object.class)).thenReturn(deserializer);
        MethodMessageHandler methodConsumer = new MethodMessageHandler(serDeFactory, bean, method);


        when(deserializer.fromMessage(message)).thenReturn(null);

        methodConsumer.onMessage(message);
    }

    @Test(expected = IllegalStateException.class)
    public void onMessageBadMethodInvocation() {

        when(serDeFactory.createDeserializer(Object.class)).thenReturn(deserializer);
        MethodMessageHandler methodConsumer = new MethodMessageHandler(serDeFactory, bean, method);
        InjectionUtils.injectInto(ReflectionUtils.findMethod(TestConsumer.class, "somePrivateMethod", Object.class), methodConsumer, "method");
        String object = "object";

        when(deserializer.fromMessage(message)).thenReturn(object);

        methodConsumer.onMessage(message);
    }



    @Test(expected = IllegalArgumentException.class)
    public void nullMethod() {
        new MethodMessageHandler(serDeFactory, bean, null);
    }

    @Test(expected = IllegalStateException.class)
    public void notPublicMethod() {
        new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "somePrivateMethod", Object.class));
    }

    @Test(expected = IllegalStateException.class)
    public void noArgs() {
        new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "noArgs"));
    }

    @Test(expected = IllegalStateException.class)
    public void moreThanTwoArgs() {
        new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "threeArgs", String.class, int.class, Message.class));
    }

    @Test(expected = IllegalStateException.class)
    public void twoMessageArgs() {
        new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "twoMessageArgs", Message.class, Message.class));
    }

    @Test(expected = IllegalStateException.class)
    public void twoNonMessageArgs() {
        new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "twoArgs", String.class, int.class));
    }


    @Test(expected = IllegalStateException.class)
    public void notVoidReturnType() {
        new MethodMessageHandler(serDeFactory, bean, ReflectionUtils.findMethod(TestConsumer.class, "toString"));
    }


    public static class TestConsumer {

        private Optional<Object> receivedObject = Optional.empty();
        private Optional<Message> receivedMessage = Optional.empty();


        public void onObject(Object object) {
            receivedObject = Optional.of(object);
        }


        public void onMessage(Message message) {
            receivedMessage = Optional.of(message);
        }


        public void onObjectAndMessage(Object object, Message message) {
            receivedObject = Optional.of(object);
            receivedMessage = Optional.of(message);
        }


        public void onMessageAndObject(Message message, Object object) {
            receivedObject = Optional.of(object);
            receivedMessage = Optional.of(message);
        }

        public Optional<Object> getReceivedObject() {
            return receivedObject;
        }

        public Optional<Message> getReceivedMessage() {
            return receivedMessage;
        }


        private void somePrivateMethod(Object object) {

        }


        public String toString() {
            return "TestConsumer";
        }


        public void noArgs() {

        }


        public void twoArgs(String name, int age) {

        }


        public void threeArgs(String name, int age, Message message) {

        }


        public void twoMessageArgs(Message message1, Message message2) {

        }

    }
}
