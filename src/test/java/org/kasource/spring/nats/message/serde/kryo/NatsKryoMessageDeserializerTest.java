package org.kasource.spring.nats.message.serde.kryo;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import io.nats.client.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.integration.kryo.Project;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class NatsKryoMessageDeserializerTest {
    @Mock
    private KryoFactory kryoFactory;

    @Mock
    private Kryo kryo;

    @Mock
    private Object object;

    @Mock
    private RuntimeException runtimeException;

    @Mock
    private MessageObjectValidator validator;


    @Mock
    private Message message;

    private Class<?> ofType = Project.class;
    private NatsKryoMessageDeserializer deserializer;

    @Before
    public void setup() {
        deserializer = new NatsKryoMessageDeserializer(ofType, kryoFactory);
    }

    @Test
    public void fromMessage() {
        byte[] data = "data".getBytes();
        when(kryoFactory.createFor(ofType)).thenReturn(kryo);
        when(message.getData()).thenReturn(data);
        when(kryo.readObject(isA(Input.class), eq(ofType))).thenAnswer(a -> object);

        assertThat(deserializer.fromMessage(message), is(equalTo(object)));
    }

    @Test
    public void fromMessageValidate() {
        deserializer.setValidator(Optional.of(validator));
        byte[] data = "data".getBytes();

        when(kryoFactory.createFor(ofType)).thenReturn(kryo);
        when(message.getData()).thenReturn(data);
        when(kryo.readObject(isA(Input.class), eq(ofType))).thenAnswer(a -> object);
        when(validator.shouldValidate(object.getClass())).thenReturn(true);

        assertThat(deserializer.fromMessage(message), is(equalTo(object)));

        verify(validator).validate(object);
    }

    @Test(expected = DeserializeException.class)
    public void exception() {
        byte[] data = "data".getBytes();
        when(kryoFactory.createFor(ofType)).thenReturn(kryo);
        when(message.getData()).thenReturn(data);
        doThrow(runtimeException).when(kryo).readObject(isA(Input.class), eq(ofType));

        deserializer.fromMessage(message);
    }
}
