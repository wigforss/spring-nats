package org.kasource.spring.nats.message.serde.json;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsJacksonMessageSerializerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ObjectWriter objectWriter;

    @Mock
    private MessageObjectValidator objectValidator;

    @Mock
    private JsonSchemaValidator schemaValidator;

    @Mock
    private Object object;


    @Test
    public void serializeWithValidation() throws JsonProcessingException {

        byte[] jsonBytes = "json".getBytes(StandardCharsets.UTF_8);

        when(objectMapper.writer()).thenReturn(objectWriter);

        NatsJacksonMessageSerializer serializer = new NatsJacksonMessageSerializer(objectMapper, Optional.of(schemaValidator));
        serializer.setValidator(Optional.of(objectValidator));
        when(objectValidator.shouldValidate(object.getClass())).thenReturn(true);

        when(objectWriter.writeValueAsBytes(object)).thenReturn(jsonBytes);

        byte[] serializedData = serializer.toMessageData(object);

        verify(objectValidator).validate(object);
        verify(schemaValidator).validate(jsonBytes, object.getClass());

        assertThat(serializedData, is(equalTo(jsonBytes)));
    }


    @Test
    public void serialize() throws JsonProcessingException {

        byte[] jsonBytes = "json".getBytes(StandardCharsets.UTF_8);

        when(objectMapper.writer()).thenReturn(objectWriter);

        NatsJacksonMessageSerializer serializer = new NatsJacksonMessageSerializer(objectMapper, Optional.empty());

        when(objectWriter.writeValueAsBytes(object)).thenReturn(jsonBytes);

        byte[] serializedData = serializer.toMessageData(object);

        verifyZeroInteractions(objectValidator, schemaValidator);

        assertThat(serializedData, is(equalTo(jsonBytes)));
    }

}
