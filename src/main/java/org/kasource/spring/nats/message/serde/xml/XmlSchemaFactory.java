package org.kasource.spring.nats.message.serde.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

public class XmlSchemaFactory {
    private SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    private Map<Class<?>, Optional<Schema>> schemasPerClass = new ConcurrentHashMap<>();

    public Optional<Schema> schemaFor(Class<?> clazz) {
        Optional<Schema> schema = schemasPerClass.get(clazz);
        if (schema == null) {
            schema = loadSchema(clazz);
            schemasPerClass.put(clazz, schema);
        }

        return schema;
    }


    private Optional<Schema> loadSchema(Class<?> clazz) {
        XmlSchema xmlSchema = clazz.getPackage().getAnnotation(XmlSchema.class);
        if (xmlSchema != null && !XmlSchema.NO_LOCATION.equals(xmlSchema.location())) {
            try {
                return loadSchema(xmlSchema.location());
            } catch (SAXException e) {
                throw new IllegalStateException("Could not parse XML Schema from " + xmlSchema.location(), e);
            }
        } else {
            return Optional.empty();
        }
    }

    private Optional<Schema> loadSchema(String location) throws SAXException {
        try {
            return Optional.of(sf.newSchema(new URL(location)));
        } catch (MalformedURLException e) {
            // Try file
            return Optional.of(sf.newSchema(new File(location)));
        }
    }
}
