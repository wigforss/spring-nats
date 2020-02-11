package org.kasource.spring.nats.message.serde.json;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsGsonMessageSerializerTest {

    private Gson gson = new Gson();

    @Mock
    private MessageObjectValidator objectValidator;



    @Test
    public void serializeWithValidation() {
        String object = "object";
        byte[] jsonBytes = ("\"" + object + "\"").getBytes(StandardCharsets.UTF_8);
        NatsGsonMessageSerializer serializer = new NatsGsonMessageSerializer(gson);
        serializer.setValidator(Optional.of(objectValidator));
        when(objectValidator.shouldValidate(String.class)).thenReturn(true);

        byte[] serializedData = serializer.toMessageData(object);

        verify(objectValidator).validate(object);


        assertThat(serializedData, is(equalTo(jsonBytes)));
    }

    @Test
    public void serialize() {
        String object = "object";
        byte[] jsonBytes = ("\"" + object + "\"").getBytes(StandardCharsets.UTF_8);
        NatsGsonMessageSerializer serializer = new NatsGsonMessageSerializer(gson);

        byte[] serializedData = serializer.toMessageData(object);

        verifyZeroInteractions(objectValidator);

        assertThat(serializedData, is(equalTo(jsonBytes)));
    }
}
