package org.kasource.spring.nats.message.serde.thrift;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nats.client.Message;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.integration.thrift.CrossPlatformResource;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ThriftMessageDeserializerTest {
    @Mock
    private Message message;

    @Mock
    private MessageObjectValidator validator;

    private ThriftMessageDeserializer deserializer = new ThriftMessageDeserializer(CrossPlatformResource.class);

    @Test(expected = IllegalArgumentException.class)
    public void illegalType() {
        new ThriftMessageDeserializer(IllegalType.class);
    }

    @Test(expected = DeserializeException.class)
    public void fromMessageInvalidData() {
        byte[] data = "data".getBytes();

        when(message.getData()).thenReturn(data);

        deserializer.fromMessage(message);
    }

    @Test
    public void fromMessage() throws TException {
        CrossPlatformResource crossPlatformResource = new CrossPlatformResource(5, "name");
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());

        byte[] data = serializer.serialize(crossPlatformResource);

        when(message.getData()).thenReturn(data);

        assertThat(deserializer.fromMessage(message), is(equalTo(crossPlatformResource)));
    }

    @Test
    public void fromMessageValidation() throws TException {
        deserializer.setValidator(Optional.of(validator));
        CrossPlatformResource crossPlatformResource = new CrossPlatformResource(5, "name");
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());

        byte[] data = serializer.serialize(crossPlatformResource);
        when(validator.shouldValidate(crossPlatformResource.getClass())).thenReturn(true);

        when(message.getData()).thenReturn(data);

        assertThat(deserializer.fromMessage(message), is(equalTo(crossPlatformResource)));

        verify(validator).validate(crossPlatformResource);
    }

    private static class IllegalType extends CrossPlatformResource {
        private IllegalType(String name) {

        }

    }
}
