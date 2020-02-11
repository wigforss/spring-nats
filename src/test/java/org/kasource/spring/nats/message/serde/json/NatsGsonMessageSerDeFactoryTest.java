package org.kasource.spring.nats.message.serde.json;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class NatsGsonMessageSerDeFactoryTest {

    private Gson gson = new Gson();

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private MessageObjectValidator objectValidator;

    @Mock
    private Map<Class<?>, NatsMessageDeserializer> deserializers;

    @Mock
    private NatsMessageDeserializer natsMessageDeserializer;

    @Captor
    private ArgumentCaptor<NatsMessageDeserializer> deserializeraptor;

    @InjectMocks
    private NatsGsonMessageSerDeFactory factory;

    @Before
    public void setup() {
    }

    @Test
    public void useGson() {
        InjectionUtils.injectInto(gson, factory, "gson");
        factory.afterPropertiesSet();

        verifyZeroInteractions(applicationContext);
    }

    @Test
    public void lookupGson() {

        factory.afterPropertiesSet();

        verify(applicationContext).getBean(Gson.class);
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
