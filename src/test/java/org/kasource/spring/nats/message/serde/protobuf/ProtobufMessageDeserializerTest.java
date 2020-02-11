package org.kasource.spring.nats.message.serde.protobuf;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nats.client.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.integration.proto.AddressBookProtos;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ProtobufMessageDeserializerTest {
    @Mock
    private Message message;

    @Mock
    private MessageObjectValidator validator;

    private ProtobufMessageDeserializer deserializer = new ProtobufMessageDeserializer(AddressBookProtos.Person.class);


    @Test(expected = DeserializeException.class)
    public void illegalMethod() throws NoSuchMethodException {

        InjectionUtils.injectInto(IllegalClass.class.getDeclaredMethod("somePrivate"), deserializer, "method");
        AddressBookProtos.Person person = AddressBookProtos.Person.newBuilder()
                .setId(5)
                .setName("name")
                .build();

        byte[] data = person.toByteArray();

        when(message.getData()).thenReturn(data);

        deserializer.fromMessage(message);

    }

    @Test(expected = DeserializeException.class)
    public void fromMessageInvalidData() {
        byte[] data = "data".getBytes();
        when(message.getData()).thenReturn(data);
        deserializer.fromMessage(message);


    }

    @Test
    public void fromMessage() {
        AddressBookProtos.Person person = AddressBookProtos.Person.newBuilder()
                .setId(5)
                .setName("name")
                .build();

        byte[] data = person.toByteArray();
        when(message.getData()).thenReturn(data);

        assertThat(deserializer.fromMessage(message), is(equalTo(person)));
    }

    @Test
    public void fromMessageValidate() {
        deserializer.setValidator(Optional.of(validator));
        AddressBookProtos.Person person = AddressBookProtos.Person.newBuilder()
                .setId(5)
                .setName("name")
                .build();

        byte[] data = person.toByteArray();

        when(validator.shouldValidate(person.getClass())).thenReturn(true);
        when(message.getData()).thenReturn(data);

        assertThat(deserializer.fromMessage(message), is(equalTo(person)));

        verify(validator).validate(person);
    }


    private static class IllegalClass {
        private static void somePrivate() {

        }
    }
}
