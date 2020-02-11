package org.kasource.spring.nats;

import io.nats.client.Message;

public class NatsResponse<T> {
    private final T payload;
    private final Message message;

    public NatsResponse(final T payload, final Message message) {
        this.payload = payload;
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }

    public Message getMessage() {
        return message;
    }
}
