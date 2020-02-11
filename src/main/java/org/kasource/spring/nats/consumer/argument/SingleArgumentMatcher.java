package org.kasource.spring.nats.consumer.argument;

import java.util.function.Supplier;

import io.nats.client.Message;

public class SingleArgumentMatcher implements ArgumentMatcher {

    private boolean isMessage;

    public SingleArgumentMatcher(boolean isMessage) {
        this.isMessage = isMessage;
    }

    @Override
    public Object[] toArgs(Supplier<Object> objectSupplier, Message message) {
        if (isMessage) {
            return new Object[]{message};
        } else {
            return new Object[]{objectSupplier.get()};
        }
    }
}
