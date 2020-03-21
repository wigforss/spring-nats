package org.kasource.spring.nats.message.serde.java;

import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavaMessageSerializerTest {
    @Mock
    private MessageObjectValidator validator;


    private JavaMessageSerializer serializer = new JavaMessageSerializer();

    @Test
    public void toMessageData() {
        String object = "object";
        serializer.setValidator(Optional.empty());
        assertThat(new String(serializer.toMessageData(object)), containsString(object));
    }

    @Test
    public void toMessageDataValidate() {
        String object = "object";
        serializer.setValidator(Optional.of(validator));

        when(validator.shouldValidate(String.class)).thenReturn(true);
        assertThat(new String(serializer.toMessageData(object)), containsString(object));

        verify(validator).validate(object);
    }

    @Test(expected = SerializeException.class)
    public void exception() {
        Object object = new Object();
        serializer.setValidator(Optional.empty());
        serializer.toMessageData(object);
    }
}
