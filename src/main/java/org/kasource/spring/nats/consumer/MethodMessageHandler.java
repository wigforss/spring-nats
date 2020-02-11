package org.kasource.spring.nats.consumer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.kasource.commons.reflection.filter.MethodFilter;
import org.kasource.commons.reflection.filter.builder.MethodFilterBuilder;
import org.kasource.spring.nats.annotation.Consumer;
import org.kasource.spring.nats.consumer.argument.ArgumentMatcher;
import org.kasource.spring.nats.consumer.argument.SingleArgumentMatcher;
import org.kasource.spring.nats.consumer.argument.TwoArgumentMatcher;
import org.kasource.spring.nats.exception.MethodInvocationException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;

import io.nats.client.Message;
import io.nats.client.MessageHandler;


public class MethodMessageHandler implements MessageHandler {
    private static final String ERROR_MESSAGE = "Method %s must be "
            + "public method, with return type void having one or two parameters.";

    private static final String ERROR_MESSAGE_TWO_ARGS = "One of the parameters of method %s must be of type "
            + Message.class.getName();

    private Object bean;
    private Method method;
    private NatsMessageDeserializer deserializer;
    private ArgumentMatcher argumentMatcher;


    public MethodMessageHandler(final NatsMessageSerDeFactory serDeFactory,
                                final Object bean,
                                final Method method) {
        this.method = method;
        this.bean = bean;
        validate();
        Optional<Class<?>> messagePayloadType = resolveMessagePayloadType();
        messagePayloadType.ifPresent(t -> this.deserializer = serDeFactory.createDeserializer(t));
    }

    private void validate() {
        if (method == null) {
            throw new IllegalArgumentException("Method may not be null");
        }
        validateMethod();

        if (method.getParameterCount() == 2) {
            validateTwoArgsMethod();
        }

        boolean isMessageArg = Message.class.isAssignableFrom(method.getParameterTypes()[0]);
        if (method.getParameterCount() == 1) {
            argumentMatcher = new SingleArgumentMatcher(isMessageArg);
        } else {
            argumentMatcher = new TwoArgumentMatcher(isMessageArg);
        }

    }

    private Optional<Class<?>> resolveMessagePayloadType() {
        boolean isMessageArg = Message.class.isAssignableFrom(method.getParameterTypes()[0]);
        if (method.getParameterCount() == 1 && !isMessageArg) {
            return Optional.of(method.getParameterTypes()[0]);
        } else if (method.getParameterCount() == 2) {
            if (isMessageArg) {
                return Optional.of(method.getParameterTypes()[1]);
            } else {
                return Optional.of(method.getParameterTypes()[0]);
            }
        }
        return Optional.empty();
    }


    private void validateMethod() {
        MethodFilter methodFilter = new MethodFilterBuilder()
                .isPublic()
                .returnType(Void.TYPE)
                .numberOfParameters(1).or().numberOfParameters(2)
                .build();

        if (!methodFilter.apply(method)) {
            throw new IllegalStateException(String.format(ERROR_MESSAGE, method));
        }
    }


    private void validateTwoArgsMethod() {
        MethodFilter methodFilter = new MethodFilterBuilder()
                .parametersExtendsType(Message.class, Object.class)
                .or()
                .parametersExtendsType(Object.class, Message.class)
                .not()
                .parametersExtendsType(Message.class, Message.class)
                .build();
        if (!methodFilter.apply(method)) {
            throw new IllegalStateException(String.format(ERROR_MESSAGE_TWO_ARGS, method));
        }
    }


    @SuppressWarnings("PMD.PreserveStackTrace")
    @Override
    public void onMessage(Message message) {
        try {
            method.invoke(bean, argumentMatcher.toArgs(() -> deserializer.fromMessage(message), message));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Can't invoke @" + Consumer.class.getName() + " annotated method " + method, e);
        } catch (InvocationTargetException e) {
            throw new MethodInvocationException("Can't invoke @" + Consumer.class.getName()
                    + " annotated method " + method + " with deserializer " + deserializer, e.getCause());
        }
    }

    @Override
    public String toString() {
        return "MethodConsumer{"
                + "method=" + method
                + '}';
    }

}
