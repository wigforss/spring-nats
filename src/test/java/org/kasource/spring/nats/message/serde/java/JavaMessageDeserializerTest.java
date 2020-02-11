package org.kasource.spring.nats.message.serde.java;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nats.client.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavaMessageDeserializerTest {

    @Mock
    private MessageObjectValidator validator;

    @Mock
    private Message message;

    private JavaMessageDeserializer deserializer = new JavaMessageDeserializer();

    @Test
    public void fromMessage() throws IOException {
        String data = "data";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(data);
        oos.flush();
        byte[] dataArray = bos.toByteArray();

        when(message.getData()).thenReturn(dataArray);

        deserializer.setValidator(Optional.empty());
        assertThat(deserializer.fromMessage(message), is(equalTo(data)));
    }

    @Test
    public void fromMessageValidate() throws IOException {
        String data = "data";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(data);
        oos.flush();
        byte[] dataArray = bos.toByteArray();

        when(message.getData()).thenReturn(dataArray);
        when(validator.shouldValidate(String.class)).thenReturn(true);

        deserializer.setValidator(Optional.of(validator));
        assertThat(deserializer.fromMessage(message), is(equalTo(data)));

        verify(validator).validate(isA(String.class));
    }

    @Test(expected = DeserializeException.class)
    public void exception() {
        String data = "data";

        when(message.getData()).thenReturn(data.getBytes());

        deserializer.setValidator(Optional.empty());
        assertThat(deserializer.fromMessage(message), is(equalTo(data)));
    }

}
