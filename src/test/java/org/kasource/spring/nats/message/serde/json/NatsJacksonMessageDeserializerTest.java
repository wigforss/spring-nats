package org.kasource.spring.nats.message.serde.json;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import io.nats.client.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsJacksonMessageDeserializerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectReader objectReader;

    @Mock
    private MessageObjectValidator objectValidator;

    @Mock
    private JsonSchemaValidator schemaValidator;

    @Mock
    private Message message;

    @Mock
    private Object object;


    @Test
    public void deserializeWithValidation() throws IOException {
        when(objectMapper.readerFor(Object.class)).thenReturn(objectReader);
        when(objectValidator.shouldValidate(Object.class)).thenReturn(true);

        NatsJacksonMessageDeserializer deserializer = new NatsJacksonMessageDeserializer(
                objectMapper,
                Object.class,
                Optional.ofNullable(schemaValidator)
        );
        deserializer.setValidator(Optional.of(objectValidator));
        String dataString = "json";
        byte[] data = dataString.getBytes(StandardCharsets.UTF_8);


        when(message.getData()).thenReturn(data);
        when(objectReader.readValue(dataString)).thenReturn(object);

        Object deserializedObject = deserializer.fromMessage(message);


        verify(objectValidator).validate(object);

        assertThat(deserializedObject, is(equalTo(object)));
    }

    @Test
    public void deserialize() throws IOException {
        when(objectMapper.readerFor(Object.class)).thenReturn(objectReader);

        NatsJacksonMessageDeserializer deserializer = new NatsJacksonMessageDeserializer(
                objectMapper,
                Object.class,
                Optional.empty()
        );
        String dataString = "json";
        byte[] data = dataString.getBytes(StandardCharsets.UTF_8);


        when(message.getData()).thenReturn(data);
        when(objectReader.readValue(dataString)).thenReturn(object);

        Object deserializedObject = deserializer.fromMessage(message);

        assertThat(deserializedObject, is(equalTo(object)));
    }
}
