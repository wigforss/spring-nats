package org.kasource.spring.nats;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.kasource.spring.nats.exception.RequestFailedException;
import org.kasource.spring.nats.exception.RequestTimeoutException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;

import io.nats.client.Connection;
import io.nats.client.Message;


public class NatsTemplateImpl implements NatsTemplate {

    private Connection natsConnection;
    private NatsMessageSerializer messageSerializer;
    private NatsMessageSerDeFactory serDeFactory;


    public NatsTemplateImpl(final Connection natsConnection,
                            final NatsMessageSerDeFactory serDeFactory) {
        this.natsConnection = natsConnection;
        this.serDeFactory = serDeFactory;
        this.messageSerializer = serDeFactory.createSerializer();
    }

    @Override
    public void publish(Object object, String subject) {

        natsConnection.publish(subject, messageSerializer.toMessageData(object));
    }

    @Override
    public void publish(Object object, String subject, String replyTo) {
        natsConnection.publish(subject, replyTo, messageSerializer.toMessageData(object));
    }

    @Override
    public <T> T requestForObject(Object requestObject,
                                  String subject,
                                  Class<T> responseType,
                                  long timeout,
                                  TimeUnit timeUnit) {
        NatsMessageDeserializer deserializer = serDeFactory.createDeserializer(responseType);
        CompletableFuture<T> responseFuture = natsConnection.request(subject, messageSerializer.toMessageData(requestObject))
                .thenApply(m -> (T) deserializer.fromMessage(m));
        try {
            if (timeout >= 0) {
                return responseFuture.get(timeout, timeUnit);
            } else {
                return responseFuture.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RequestFailedException("Request failed for subject " + subject, e);
        } catch (TimeoutException e) {
            throw new RequestTimeoutException("Request timed out after " + timeout + " "
                    + timeUnit.name().toLowerCase(Locale.US) + " for subject " + subject, e);
        }
    }

    @Override
    public <T> CompletableFuture<T> requestForObjectAsync(Object requestObject,
                                                          String subject,
                                                          Class<T> responseType) {
        final NatsMessageDeserializer deserializer = serDeFactory.createDeserializer(responseType);
        CompletableFuture<Message> responseFuture = natsConnection.request(subject, messageSerializer.toMessageData(requestObject));
        return responseFuture.thenApplyAsync(m -> (T) deserializer.fromMessage(m));
    }

    @Override
    public Message requestForMessage(Object requestObject,
                                     String subject,
                                     long timeout,
                                     TimeUnit timeUnit) {
        try {
            CompletableFuture<Message> responseFuture = natsConnection.request(subject, messageSerializer.toMessageData(requestObject));
            if (timeout >= 0) {
                return responseFuture.get(timeout, timeUnit);
            } else {
                return responseFuture.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RequestFailedException("Request failed for subject " + subject, e);
        } catch (TimeoutException e) {
            throw new RequestTimeoutException("Request timed out after " + timeout + " "
                    + timeUnit.name().toLowerCase(Locale.US) + " for subject " + subject, e);
        }
    }

    @Override
    public CompletableFuture<Message> requestForMessageAsync(Object requestObject,
                                                             String subject) {
        return natsConnection.request(subject, messageSerializer.toMessageData(requestObject));
    }

    @Override
    public <T> NatsResponse<T> request(Object requestObject,
                                       String subject,
                                       Class<T> responseType,
                                       long timeout,
                                       TimeUnit timeUnit) {
        NatsMessageDeserializer deserializer = serDeFactory.createDeserializer(responseType);
        CompletableFuture<NatsResponse> responseFuture = natsConnection.request(subject, messageSerializer.toMessageData(requestObject))
                .thenApply(m -> new NatsResponse<>((T) deserializer.fromMessage(m), m));
        try {
            if (timeout >= 0) {
                return responseFuture.get(timeout, timeUnit);
            } else {
                return responseFuture.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RequestFailedException("Request failed for subject " + subject, e);
        } catch (TimeoutException e) {
            throw new RequestTimeoutException("Request timed out after " + timeout + " "
                    + timeUnit.name().toLowerCase(Locale.US) + " for subject " + subject, e);
        }
    }

    @Override
    public <T> CompletableFuture<NatsResponse<T>> requestAsync(Object requestObject,
                                                               String subject,
                                                               Class<T> responseType) {
        NatsMessageDeserializer deserializer = serDeFactory.createDeserializer(responseType);
        CompletableFuture<Message> responseFuture = natsConnection.request(subject, messageSerializer.toMessageData(requestObject));
        return responseFuture.thenApplyAsync(m -> new NatsResponse<>((T) deserializer.fromMessage(m), m));
    }

}
