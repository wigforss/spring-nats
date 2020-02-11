package org.kasource.spring.nats.message.serde.protobuf;


import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.integration.proto.AddressBookProtos;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProtobufMessageSerDeFactoryTest {
    @Mock
    private ProtobufMessageSerializer serializer;
    @Mock
    private Map<Class<?>, NatsMessageDeserializer> deserializers;
    @Mock
    private MessageObjectValidator validator;

    @Mock
    private NatsMessageDeserializer deserializer;

    @Captor
    private ArgumentCaptor<NatsMessageDeserializer> deserializerCaptor;

    @InjectMocks
    private ProtobufMessageSerDeFactory factory;

    @Test
    public void afterPropertiesSet() {

        factory.afterPropertiesSet();

        verify(serializer).setValidator(Optional.ofNullable(validator));
    }

    @Test
    public void createSerializer() {
        assertThat(factory.createSerializer(), is(equalTo(serializer)));
    }

    @Test(expected = IllegalStateException.class)
    public void createDeserializerForIllegalType() {
        factory.createDeserializer(String.class);
    }

    @Test
    public void createDeserializerCached() {
        when(deserializers.get(AddressBookProtos.Person.class)).thenReturn(deserializer);

        assertThat(factory.createDeserializer(AddressBookProtos.Person.class), is(equalTo(deserializer)));
    }

    @Test
    public void createDeserializer() {
        NatsMessageDeserializer response = factory.createDeserializer(AddressBookProtos.Person.class);

        verify(deserializers).put(eq(AddressBookProtos.Person.class), deserializerCaptor.capture());

        assertThat(response, is(equalTo(deserializerCaptor.getValue())));

    }
}
