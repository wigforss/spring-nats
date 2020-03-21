package org.kasource.spring.nats.message.serde.thrift;

import java.util.Map;
import java.util.Optional;

import org.kasource.spring.nats.integration.thrift.CrossPlatformResource;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ThriftMessageSerDeFactoryTest {
    @Mock
    private ThriftMessageSerializer serializer;
    @Mock
    private Map<Class<?>, NatsMessageDeserializer> deserializers;
    @Mock
    private ThriftMessageDeserializer deserializer;

    @Mock
    private MessageObjectValidator validator;

    @Captor
    private ArgumentCaptor<ThriftMessageDeserializer> deserializerCaptor;

    @InjectMocks
    private ThriftMessageSerDeFactory factory;

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
        when(deserializers.get(CrossPlatformResource.class)).thenReturn(deserializer);

        assertThat(factory.createDeserializer(CrossPlatformResource.class), is(equalTo(deserializer)));
    }

    @Test
    public void createDeserializer() {
        NatsMessageDeserializer response = factory.createDeserializer(CrossPlatformResource.class);

        verify(deserializers).put(eq(CrossPlatformResource.class), deserializerCaptor.capture());

        assertThat(response, is(equalTo(deserializerCaptor.getValue())));

    }
}
