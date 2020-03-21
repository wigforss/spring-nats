package org.kasource.spring.nats.message.serde.avro;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.integration.avro.User;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AvroMessageSerializerTest {
    @Mock
    private GenericDatumWriter datumWriter;

    @Mock
    private Map<Class<?>, GenericDatumWriter> writers;

    @Mock
    private User user;

    @Mock
    private MessageObjectValidator objectValidator;

    @Mock
    private IOException ioException;

    @InjectMocks
    private AvroMessageSerializer serializer;

    @Test
    public void noValidation() throws IOException {
        String data = "data";

        serializer.setValidator(Optional.empty());

        when(writers.get(user.getClass())).thenReturn(datumWriter);

        doAnswer((a) -> {
            ((BinaryEncoder) a.getArgument(1)).writeBytes(data.getBytes());
            return null;
        })
                .when(datumWriter).write(eq(user), isA(BinaryEncoder.class));

        assertThat(serializer.toMessageData(user), is(equalTo(("\b" + data).getBytes())));

        verify(datumWriter).write(eq(user), isA(BinaryEncoder.class));
    }


    @Test
    public void withValidation() throws IOException {
        String data = "data";

        serializer.setValidator(Optional.of(objectValidator));
        when(objectValidator.shouldValidate(user.getClass())).thenReturn(true);
        when(writers.get(user.getClass())).thenReturn(datumWriter);

        doAnswer((a) -> {
            ((BinaryEncoder) a.getArgument(1)).writeBytes(data.getBytes());
            return null;
        })
                .when(datumWriter).write(eq(user), isA(BinaryEncoder.class));

        assertThat(serializer.toMessageData(user), is(equalTo(("\b" + data).getBytes())));
        verify(objectValidator).validate(user);
        verify(datumWriter).write(eq(user), isA(BinaryEncoder.class));

    }

    @Test(expected = SerializeException.class)
    public void exception() throws IOException {
        serializer.setValidator(Optional.empty());

        when(writers.get(user.getClass())).thenReturn(datumWriter);

        doThrow(ioException)
                .when(datumWriter).write(eq(user), isA(BinaryEncoder.class));

        serializer.toMessageData(user);

    }


}
