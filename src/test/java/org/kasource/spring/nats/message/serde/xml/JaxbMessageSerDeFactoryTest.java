package org.kasource.spring.nats.message.serde.xml;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
public class JaxbMessageSerDeFactoryTest {
    @Mock
    private JaxbMessageSerializer serializer;
    @Mock
    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();
    @Mock
    private ApplicationContext applicationContext;
    @Mock
    private JAXBContext jaxbContext;
    @Mock
    private MessageObjectValidator validator;
    @Mock
    private NoSuchBeanDefinitionException noSuchBeanDefinitionException;
    @Mock
    private NatsMessageDeserializer cachedDeserializer;

    @Captor
    private ArgumentCaptor<NatsMessageDeserializer> deserializerCaptor;
    @InjectMocks
    private JaxbMessageSerDeFactory factory;

    @Test
    public void afterPropertiesSet() {
        InjectionUtils.injectInto(null, factory, "jaxbContext");

        when(applicationContext.getBean(JAXBContext.class)).thenReturn(jaxbContext);

        factory.afterPropertiesSet();

        verify(serializer).setJaxbContext(Optional.of(jaxbContext));
        verify(serializer).setValidator(Optional.ofNullable(validator));
    }

    @Test
    public void afterPropertiesSetNoJaxbContext() {
        InjectionUtils.injectInto(null, factory, "jaxbContext");

        doThrow(noSuchBeanDefinitionException).when(applicationContext).getBean(JAXBContext.class);

        factory.afterPropertiesSet();


        verify(serializer).setValidator(Optional.ofNullable(validator));
    }

    @Test
    public void createSerializer() {
        assertThat(factory.createSerializer(), is(equalTo(serializer)));
    }

    @Test
    public void createDeserializerCached() {
        Class<?> forClass = String.class;
        when(deserializers.get(forClass)).thenReturn(cachedDeserializer);

        assertThat(factory.createDeserializer(forClass), is(equalTo(cachedDeserializer)));
    }

    @Test
    public void createDeserializer() {
        Class<?> forClass = String.class;
        when(deserializers.get(forClass)).thenReturn(null);

        NatsMessageDeserializer deserializer = factory.createDeserializer(forClass);

        verify(deserializers).put(eq(forClass), deserializerCaptor.capture());

        assertThat(deserializer, is(equalTo(deserializerCaptor.getValue())));
    }
}
