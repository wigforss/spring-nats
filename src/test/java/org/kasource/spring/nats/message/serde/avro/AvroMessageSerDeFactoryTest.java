package org.kasource.spring.nats.message.serde.avro;

import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.integration.avro.User;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AvroMessageSerDeFactoryTest {

    @Mock
    private AvroMessageSerializer serializer;

    @Mock
    private Map<Class<?>, NatsMessageDeserializer> deserializers;

    @Mock
    private MessageObjectValidator validator;

    @Mock
    private NatsMessageDeserializer cachedDeserializer;

    @InjectMocks
    private AvroMessageSerDeFactory factory;

    @Test
    public void afterPropertiesSet() throws Exception{
        factory.afterPropertiesSet();

        verify(serializer).setValidator(Optional.of(validator));
    }

    @Test
    public void createSerializer() {
        assertThat(factory.createSerializer(), is(equalTo(serializer)));
    }

    @Test
    public void createDeserializer() {
        NatsMessageDeserializer deserializer = factory.createDeserializer(User.class);
        assertThat(deserializer, is(notNullValue()));
        verify(deserializers).put(eq(User.class), isA(NatsMessageDeserializer.class));
    }

    @Test
    public void createDeserializerFromCache() {
        when(deserializers.get(User.class)).thenReturn(cachedDeserializer);
        NatsMessageDeserializer deserializer = factory.createDeserializer(User.class);
        assertThat(deserializer, is(equalTo(cachedDeserializer)));

    }
}
