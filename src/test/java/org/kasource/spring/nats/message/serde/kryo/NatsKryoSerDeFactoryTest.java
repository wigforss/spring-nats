package org.kasource.spring.nats.message.serde.kryo;

import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsKryoSerDeFactoryTest {

    @Mock
    private KryoFactory kryoFactory;

    @Mock
    private MessageObjectValidator validator;

    @InjectMocks
    private NatsKryoSerDeFactory factory;

    @Test
    public void createSerializer() {
        assertThat(factory.createSerializer(), is(notNullValue()));
    }

    @Test
    public void createDeserializer() {
        assertThat(factory.createDeserializer(String.class), is(notNullValue()));
    }
}
