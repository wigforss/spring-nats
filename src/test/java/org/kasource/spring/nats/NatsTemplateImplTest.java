package org.kasource.spring.nats;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.nats.client.Connection;
import io.nats.client.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsTemplateImplTest {
    @Mock
    private Connection natsConnection;
    @Mock
    private NatsMessageSerializer messageSerializer;
    @Mock
    private NatsMessageDeserializer messageDeserializer;
    @Mock
    private NatsMessageSerDeFactory serDeFactory;
    @Mock
    private CompletableFuture<Message> responseFuture;
    @Mock
    private CompletableFuture<Object> responseForObject;

    @Mock
    private CompletableFuture<NatsResponse<Object>> responseForNatsResponse;

    @Mock
    private NatsResponse<Object> natsResponse;

    @Mock
    private NatsResponse<Object> responseForAsyncNatsResponse;

    @Mock
    private Message message;

    @Captor
    private ArgumentCaptor<Function<Message, Object>> transformToObjectCapture;

    @Captor
    private ArgumentCaptor<Function<Message, NatsResponse<Object>>> transformToNatsResponseCapture;

    @Mock
    private Object requestObject;

    @Mock
    private Object responseObject;

    private NatsTemplateImpl natsTemplate;

    @Before
    public void setup() {
        when(serDeFactory.createSerializer()).thenReturn(messageSerializer);

        natsTemplate = new NatsTemplateImpl(natsConnection, serDeFactory);
    }

    @Test
    public void publishToSubject() {
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);

        natsTemplate.publish(requestObject, subject);

        verify(natsConnection).publish(subject, data);
    }

    @Test
    public void publishToSubjectAndQueue() {
        String subject = "subject";
        String queue = "queue";

        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);

        natsTemplate.publish(requestObject, subject, queue);

        verify(natsConnection).publish(subject, queue, data);
    }

    @Test
    public void requestForMessage() throws InterruptedException, ExecutionException, TimeoutException {
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);
        when(natsConnection.request(subject, data)).thenReturn(responseFuture);
        when(responseFuture.get(1, TimeUnit.SECONDS)).thenReturn(message);

        Message response = natsTemplate.requestForMessage(requestObject, subject, 1, TimeUnit.SECONDS);

        assertThat(response, is(equalTo(message)));
    }

    @Test
    public void requestForMessageAsync() {
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);
        when(natsConnection.request(subject, data)).thenReturn(responseFuture);

        CompletableFuture<Message> response = natsTemplate.requestForMessageAsync(requestObject, subject);

        assertThat(response, is(equalTo(responseFuture)));
    }

    @Test
    public void requestForObject() throws InterruptedException, ExecutionException, TimeoutException {
        Class<Object> responseType = Object.class;
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);
        when(natsConnection.request(subject, data)).thenReturn(responseFuture);
        when(responseFuture.thenApply(transformToObjectCapture.capture())).thenReturn(responseForObject);
        when(responseForObject.get(1, TimeUnit.SECONDS)).thenReturn(responseObject);
        when(serDeFactory.createDeserializer(responseType)).thenReturn(messageDeserializer);
        when(messageDeserializer.fromMessage(message)).thenReturn(responseObject);

        Object response = natsTemplate.requestForObject(requestObject, subject, responseType, 1, TimeUnit.SECONDS);

        assertThat(transformToObjectCapture.getValue().apply(message), is(equalTo(responseObject)));
        assertThat(response, is(equalTo(responseObject)));
    }

    @Test
    public void requestForObjectAsync() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Message> natsResponseFuture = new CompletableFuture<>();


        Class<Object> responseType = Object.class;
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);
        when(natsConnection.request(subject, data)).thenReturn(natsResponseFuture);
        when(serDeFactory.createDeserializer(responseType)).thenReturn(messageDeserializer);
        when(messageDeserializer.fromMessage(message)).thenReturn(responseObject);

        CompletableFuture<Object> response = natsTemplate.requestForObjectAsync(requestObject, subject, responseType);
        assertThat(response.getNow(null), is(nullValue()));
        natsResponseFuture.complete(message);

        Object actualResponse = response.get(1, TimeUnit.SECONDS);

        assertThat(actualResponse, is(equalTo(responseObject)));

    }

    @Test
    public void request() throws InterruptedException, ExecutionException, TimeoutException {
        Class<Object> responseType = Object.class;
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);
        when(natsConnection.request(subject, data)).thenReturn(responseFuture);
        when(responseFuture.thenApply(transformToNatsResponseCapture.capture())).thenReturn(responseForNatsResponse);
        when(responseForNatsResponse.get(1, TimeUnit.SECONDS)).thenReturn(natsResponse);
        when(serDeFactory.createDeserializer(responseType)).thenReturn(messageDeserializer);
        when(messageDeserializer.fromMessage(message)).thenReturn(responseObject);

        NatsResponse<Object> response = natsTemplate.request(requestObject, subject, responseType, 1, TimeUnit.SECONDS);

        assertThat(response, is(equalTo(natsResponse)));

        NatsResponse<Object> transformResult = transformToNatsResponseCapture.getValue().apply(message);

        assertThat(transformResult.getPayload(), is(equalTo(responseObject)));
        assertThat(transformResult.getMessage(), is(equalTo(message)));

    }

    @Test
    public void requestAsync() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Message> natsResponseFuture = new CompletableFuture<>();

        Class<Object> responseType = Object.class;
        String subject = "subject";
        final byte[] data = {};

        when(messageSerializer.toMessageData(requestObject)).thenReturn(data);
        when(natsConnection.request(subject, data)).thenReturn(natsResponseFuture);


        when(serDeFactory.createDeserializer(responseType)).thenReturn(messageDeserializer);
        when(messageDeserializer.fromMessage(message)).thenReturn(responseObject);

        CompletableFuture<NatsResponse<Object>> response = natsTemplate.requestAsync(requestObject, subject, responseType);
        assertThat(response.getNow(null), is(nullValue()));
        natsResponseFuture.complete(message);

        NatsResponse<Object> actualResponse = response.get(1, TimeUnit.SECONDS);

        assertThat(actualResponse.getPayload(), is(equalTo(responseObject)));
        assertThat(actualResponse.getMessage(), is(equalTo(message)));

    }

}
