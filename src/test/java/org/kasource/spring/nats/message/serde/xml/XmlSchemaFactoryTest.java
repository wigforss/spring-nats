package org.kasource.spring.nats.message.serde.xml;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Optional;

import javax.xml.bind.annotation.XmlSchema;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.kasource.spring.nats.integration.xml.Person;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.xml.sax.SAXException;

@RunWith(MockitoJUnitRunner.class)
public class XmlSchemaFactoryTest {
    @Mock
    private SchemaFactory sf;
    @Mock
    private Map<Class<?>, Optional<Schema>> schemasPerClass;
    @Mock
    private Schema schema;
    @Mock
    private SAXException saxException;

    @Captor
    private ArgumentCaptor<File> fileCaptor;

    @Captor
    private ArgumentCaptor<URL> urlCaptor;

    @InjectMocks
    private XmlSchemaFactory xmlSchemaFactory;

    @Test
    public void schemaFor() {
        Class<?> forClass = String.class;

        when(schemasPerClass.get(forClass)).thenReturn(Optional.of(schema));

        assertThat(xmlSchemaFactory.schemaFor(forClass), is(equalTo(Optional.of(schema))));
    }

    @Test
    public void schemaForNoSchema() {
        Class<?> forClass = String.class;

        assertThat(xmlSchemaFactory.schemaFor(forClass), is(Optional.empty()));

        verify(schemasPerClass).put(forClass, Optional.empty());
    }

    @Test
    public void schemaForLoadFromFileLocation() throws SAXException {
        Class<?> forClass = Person.class;

        when(sf.newSchema(fileCaptor.capture())).thenReturn(schema);

        assertThat(xmlSchemaFactory.schemaFor(forClass), is(equalTo(Optional.of(schema))));
        assertThat(forClass.getPackage().getAnnotation(XmlSchema.class).location(), is(equalTo(fileCaptor.getValue().getPath())));
        verify(schemasPerClass).put(forClass, Optional.of(schema));
    }

    @Test
    public void schemaForLoadFromUrlLocation() throws SAXException {
        Class<?> forClass = this.getClass();

        when(sf.newSchema(urlCaptor.capture())).thenReturn(schema);

        assertThat(xmlSchemaFactory.schemaFor(forClass), is(equalTo(Optional.of(schema))));
        assertThat(forClass.getPackage().getAnnotation(XmlSchema.class).location(), is(equalTo(urlCaptor.getValue().toString())));
        verify(schemasPerClass).put(forClass, Optional.of(schema));
    }

    @Test(expected = IllegalStateException.class)
    public void schemaForLoadExceptionReadingSchema() throws SAXException {
        Class<?> forClass = Person.class;

        doThrow(saxException).when(sf).newSchema(isA(File.class));

        xmlSchemaFactory.schemaFor(forClass);

    }
}
