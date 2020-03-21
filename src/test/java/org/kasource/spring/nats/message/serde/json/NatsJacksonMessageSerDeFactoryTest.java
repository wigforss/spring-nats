package org.kasource.spring.nats.message.serde.json;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class NatsJacksonMessageSerDeFactoryTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageObjectValidator objectValidator;

    @Mock
    private JsonSchemaValidator schemaValidator;

    @Mock
    private Map<Class<?>, NatsMessageDeserializer> deserializers;

    @Mock
    private NatsMessageDeserializer natsMessageDeserializer;

    @Captor
    private ArgumentCaptor<NatsMessageDeserializer> deserializeraptor;

    @InjectMocks
    private NatsJacksonMessageSerDeFactory factory;

    @Before
    public void setup() {

    }

    @Test
    public void useObjectMapper() {

        factory.afterPropertiesSet();

        verifyZeroInteractions(applicationContext);
    }

    @Test
    public void lookupObjectMaooer() {
        InjectionUtils.injectInto(null, factory, "objectMapper");

        when(applicationContext.getBean(ObjectMapper.class)).thenReturn(objectMapper);
        factory.afterPropertiesSet();

        verify(applicationContext).getBean(ObjectMapper.class);
    }

    @Test
    public void createSerializer() {
        factory.afterPropertiesSet();
        assertThat(factory.createSerializer(), is(notNullValue()));
    }

    @Test
    public void createDeserializerFromCache() {
        when(deserializers.get(String.class)).thenReturn(natsMessageDeserializer);

        NatsMessageDeserializer created = factory.createDeserializer(String.class);

        assertThat(created, is(equalTo(natsMessageDeserializer)));
    }

    @Test
    public void createDeserializer() {
        when(deserializers.get(String.class)).thenReturn(null);

        NatsMessageDeserializer created = factory.createDeserializer(String.class);

        verify(deserializers).put(eq(String.class), deserializeraptor.capture());

        assertThat(created, is(equalTo(deserializeraptor.getValue())));
    }
}
