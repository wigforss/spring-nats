package org.kasource.spring.nats.consumer.argument;

import java.util.function.Supplier;

import io.nats.client.Message;

public class TwoArgumentMatcher implements ArgumentMatcher {

    private boolean isMessage;

    public TwoArgumentMatcher(boolean isMessage) {
        this.isMessage = isMessage;
    }

    @Override
    public Object[] toArgs(Supplier<Object> objectSupplier, Message message) {
        if (isMessage) {
            return new Object[]{message, objectSupplier.get()};
        } else {
            return new Object[]{objectSupplier.get(), message};
        }

    }
}
