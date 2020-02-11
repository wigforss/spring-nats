package org.kasource.spring.nats.consumer.argument;

import java.util.function.Supplier;

import io.nats.client.Message;

public interface ArgumentMatcher {

    Object[] toArgs(Supplier<Object> objectSupplier, Message message);
}
