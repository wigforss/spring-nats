package org.kasource.spring.nats.message.serde.protobuf;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.protobuf.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProtobufMessageSerializerTest {

    @Mock
    private MessageObjectValidator validator;

    @Mock
    private Message object;

    private ProtobufMessageSerializer serializer = new ProtobufMessageSerializer();

    @Test
    public void toMessageData() {
        byte[] data = "data".getBytes();

        when(object.toByteArray()).thenReturn(data);

        assertThat(serializer.toMessageData(object), is(equalTo(data)));
    }

    @Test
    public void toMessageDataValidate() {
        serializer.setValidator(Optional.of(validator));
        byte[] data = "data".getBytes();

        when(validator.shouldValidate(object.getClass())).thenReturn(true);
        when(object.toByteArray()).thenReturn(data);

        assertThat(serializer.toMessageData(object), is(equalTo(data)));

        verify(validator).validate(object);
    }
}
