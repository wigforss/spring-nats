package org.kasource.spring.nats.message.serde.java;

import java.util.Optional;

import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class JavaMessageSerDeFactoryTest {
    @Mock
    private JavaMessageDeserializer deserializer;

    @Mock
    private JavaMessageSerializer serializer;

    @Mock
    private MessageObjectValidator validator;

    @InjectMocks
    private JavaMessageSerDeFactory factory;

    @Test
    public void afterPropertiesSet() throws Exception {
        factory.afterPropertiesSet();

        verify(deserializer).setValidator(Optional.ofNullable(validator));
        verify(serializer).setValidator(Optional.ofNullable(validator));
    }

    @Test
    public void createSerializer() {
        assertThat(factory.createSerializer(), is(equalTo(serializer)));
    }

    @Test
    public void createDeserializer() {
        assertThat(factory.createDeserializer(String.class), is(equalTo(deserializer)));
    }
}
