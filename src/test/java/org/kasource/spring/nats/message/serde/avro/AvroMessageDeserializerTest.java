package org.kasource.spring.nats.message.serde.avro;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.integration.avro.User;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.nats.client.Message;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class AvroMessageDeserializerTest {

    @Mock
    private GenericDatumReader datumReader;

    @Mock
    private Message message;

    @Mock
    private MessageObjectValidator objectValidator;

    @Mock
    private Object object;

    @Mock
    private IOException ioException;

    @Test
    public void noValidation() throws IOException {
        byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        AvroMessageDeserializer deserializer = new AvroMessageDeserializer(User.class);
        InjectionUtils.injectInto(datumReader, deserializer, "datumReader");

        when(message.getData()).thenReturn(data);
        when(datumReader.read(isNull(), isA(BinaryDecoder.class))).thenReturn(object);

        assertThat(deserializer.fromMessage(message), is(equalTo(object)));
    }

    @Test
    public void withValidation() throws IOException {
        byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        AvroMessageDeserializer deserializer = new AvroMessageDeserializer(String.class);
        deserializer.setValidator(Optional.of(objectValidator));
        InjectionUtils.injectInto(datumReader, deserializer, "datumReader");

        when(message.getData()).thenReturn(data);
        when(datumReader.read(isNull(), isA(BinaryDecoder.class))).thenReturn(object);
        when(objectValidator.shouldValidate(object.getClass())).thenReturn(true);
        assertThat(deserializer.fromMessage(message), is(equalTo(object)));

        verify(objectValidator).validate(object);
    }

    @Test(expected = DeserializeException.class)
    public void exception() throws IOException {
        byte[] data = "data".getBytes(StandardCharsets.UTF_8);
        AvroMessageDeserializer deserializer = new AvroMessageDeserializer(User.class);
        InjectionUtils.injectInto(datumReader, deserializer, "datumReader");

        when(message.getData()).thenReturn(data);
        doThrow(ioException).when(datumReader).read(isNull(), isA(BinaryDecoder.class));

        deserializer.fromMessage(message);
    }
}
