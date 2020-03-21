package org.kasource.spring.nats.message.serde.json;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import io.nats.client.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.gson.Gson;

@RunWith(MockitoJUnitRunner.class)
public class NatsGsonMessageDeserializerTest {


    private Gson gson = new Gson();

    @Mock
    private MessageObjectValidator objectValidator;


    private String object = "object";

    @Mock
    private Message message;


    @Test
    public void deserializeWithValidation() {


        NatsGsonMessageDeserializer deserializer =
                new NatsGsonMessageDeserializer(
                        gson,
                        String.class);
        deserializer.setValidator(Optional.ofNullable(objectValidator));
        byte[] data = object.getBytes(StandardCharsets.UTF_8);

        when(objectValidator.shouldValidate(String.class)).thenReturn(true);
        when(message.getData()).thenReturn(data);

        Object deSerializedObject = deserializer.fromMessage(message);

        verify(objectValidator).validate(object);

        assertThat(deSerializedObject, is(equalTo(object)));
    }

    @Test
    public void deserializeNoValidation() {


        NatsGsonMessageDeserializer deserializer =
                new NatsGsonMessageDeserializer(
                        gson,
                        String.class);
        byte[] data = object.getBytes(StandardCharsets.UTF_8);


        when(message.getData()).thenReturn(data);

        Object deserializedObject = deserializer.fromMessage(message);

        verifyZeroInteractions(objectValidator);

        assertThat(deserializedObject, is(equalTo(object)));
    }
}
