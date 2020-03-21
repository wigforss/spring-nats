package org.kasource.spring.nats.message.serde.kryo;

import java.util.Optional;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

@RunWith(MockitoJUnitRunner.class)
public class NatsKryoMessageSerializerTest {
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

    private NatsKryoMessageSerializer serializer;

    @Before
    public void setup() {
        serializer = new NatsKryoMessageSerializer(kryoFactory);
    }

    @Test
    public void fromMessage() {
        String object = "object";
        when(kryoFactory.createFor(String.class)).thenReturn(kryo);

        doAnswer((a) -> {
            ((Output) a.getArgument(0)).getOutputStream().write(object.getBytes());
            return null;
        }).when(kryo).writeObject(isA(Output.class), isA(String.class));


        assertThat(serializer.toMessageData(object), is(equalTo(object.getBytes())));
    }

    @Test
    public void fromMessageValidate() {
        serializer.setValidator(Optional.of(validator));
        String object = "object";
        when(validator.shouldValidate(String.class)).thenReturn(true);
        when(kryoFactory.createFor(String.class)).thenReturn(kryo);

        doAnswer((a) -> {
            ((Output) a.getArgument(0)).getOutputStream().write(object.getBytes());
            return null;
        }).when(kryo).writeObject(isA(Output.class), isA(String.class));


        assertThat(serializer.toMessageData(object), is(equalTo(object.getBytes())));

        verify(validator).validate(object);
    }

    @Test(expected = SerializeException.class)
    public void exception() {
        String object = "object";
        when(kryoFactory.createFor(String.class)).thenReturn(kryo);

        doThrow(runtimeException).when(kryo).writeObject(isA(Output.class), isA(String.class));


        serializer.toMessageData(object);
    }

}
