package org.kasource.spring.nats.message.serde.xml;

import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class JaxbMessageSerializerTest {
    @Mock
    private Map<Class<?>, JAXBContext> contextsPerClass;
    @Mock
    private JAXBContext jaxbContext;
    @Mock
    private XmlSchemaFactory xmlSchemaFactory;

    @Mock
    private Schema schema;
    @Mock
    private MessageObjectValidator validator;

    @Mock
    private JAXBException jaxbException;

    @Mock
    private Object object;

    @Mock
    private Marshaller marshaller;

    @InjectMocks
    private JaxbMessageSerializer serializer = new JaxbMessageSerializer();

    @Test
    public void toMessageData() throws JAXBException {
        InjectionUtils.injectInto(Optional.of(jaxbContext), serializer, "jaxbContext");

        when(jaxbContext.createMarshaller()).thenReturn(marshaller);

        serializer.toMessageData(object);

        verify(marshaller).marshal(eq(object), isA(OutputStreamWriter.class));
    }

    @Test
    public void toMessageDataXmlSchema() throws JAXBException {
        InjectionUtils.injectInto(Optional.of(jaxbContext), serializer, "jaxbContext");
        serializer.setValidator(Optional.of(validator));
        when(validator.shouldValidate(object.getClass())).thenReturn(true);
        when(xmlSchemaFactory.schemaFor(object.getClass())).thenReturn(Optional.of(schema));
        when(jaxbContext.createMarshaller()).thenReturn(marshaller);

        serializer.toMessageData(object);

        verify(validator).validate(object);
        verify(marshaller).setSchema(schema);
        verify(marshaller).marshal(eq(object), isA(OutputStreamWriter.class));
    }

    @Test(expected = SerializeException.class)
    public void toMessageDataJAXBException() throws JAXBException {
        InjectionUtils.injectInto(Optional.of(jaxbContext), serializer, "jaxbContext");

        when(jaxbContext.createMarshaller()).thenReturn(marshaller);
        doThrow(jaxbException).when(marshaller).marshal(eq(object), isA(OutputStreamWriter.class));

        serializer.toMessageData(object);


    }

}
