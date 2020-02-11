package org.kasource.spring.nats.message.serde.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.nats.client.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class JaxbMessageDeserializerTest {

    @Mock
    private JAXBContext jaxbContext;

    @Mock
    private Schema schema;

    @Mock
    private Message message;

    @Mock
    private XmlSchemaFactory xmlSchemaFactory;

    @Mock
    private Unmarshaller unmarshaller;

    @Mock
    private MessageObjectValidator validator;

    @Mock
    private Object object;

    @Mock
    private JAXBException jaxbException;

    @Mock
    private IOException ioException;


    private Class<?> forType = String.class;

    private JaxbMessageDeserializer deserializer;

    @Before
    public void setup() {
        deserializer = new JaxbMessageDeserializer(forType, Optional.of(jaxbContext));
    }

    @Test
    public void fromMessage() throws JAXBException {
        byte[] data = "data".getBytes();

        when(message.getData()).thenReturn(data);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshal(isA(ByteArrayInputStream.class))).thenReturn(object);

        assertThat(deserializer.fromMessage(message), is(equalTo(object)));
    }

    @Test
    public void fromMessageXmlSchema() throws JAXBException {
        deserializer.setValidator(Optional.of(validator));
        Optional<Schema> optionalSchema = Optional.of(schema);
        InjectionUtils.injectInto(optionalSchema, deserializer, "schema");
        byte[] data = "data".getBytes();

        when(message.getData()).thenReturn(data);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        when(unmarshaller.unmarshal(isA(ByteArrayInputStream.class))).thenReturn(object);
        when(validator.shouldValidate(object.getClass())).thenReturn(true);
        assertThat(deserializer.fromMessage(message), is(equalTo(object)));

        verify(unmarshaller).setSchema(schema);
        verify(validator).validate(object);
    }

    @Test(expected = DeserializeException.class)
    public void fromMessageJaxbException() throws JAXBException {
        byte[] data = "data".getBytes();

        when(message.getData()).thenReturn(data);
        when(jaxbContext.createUnmarshaller()).thenReturn(unmarshaller);
        doThrow(jaxbException).when(unmarshaller).unmarshal(isA(ByteArrayInputStream.class));

        deserializer.fromMessage(message);
    }

}
