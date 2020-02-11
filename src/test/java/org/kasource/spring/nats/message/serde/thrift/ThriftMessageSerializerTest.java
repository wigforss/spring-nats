package org.kasource.spring.nats.message.serde.thrift;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.integration.thrift.CrossPlatformResource;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ThriftMessageSerializerTest {

    @Mock
    private MessageObjectValidator validator;

    private ThriftMessageSerializer serializer = new ThriftMessageSerializer();

    @Test(expected = ClassCastException.class)
    public void toMessageDataInvalidObject() {
        serializer.toMessageData("data");
    }

    @Test
    public void toMessageData() {
        CrossPlatformResource crossPlatformResource = new CrossPlatformResource(5, "name");

        byte[] response = serializer.toMessageData(crossPlatformResource);

        assertThat(new String(response), containsString("name"));
    }

    @Test
    public void toMessageDataValidate() {
        serializer.setValidator(Optional.of(validator));
        CrossPlatformResource crossPlatformResource = new CrossPlatformResource(5, "name");

        when(validator.shouldValidate(crossPlatformResource.getClass())).thenReturn(true);

        byte[] response = serializer.toMessageData(crossPlatformResource);

        assertThat(new String(response), containsString("name"));

        verify(validator).validate(crossPlatformResource);
    }
}
