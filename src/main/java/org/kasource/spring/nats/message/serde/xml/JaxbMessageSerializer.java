package org.kasource.spring.nats.message.serde.xml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.validation.Schema;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

public class JaxbMessageSerializer implements NatsMessageSerializer {

    private Map<Class<?>, JAXBContext> contextsPerClass = new ConcurrentHashMap<>();
    private Optional<JAXBContext> jaxbContext = Optional.empty();
    private XmlSchemaFactory xmlSchemaFactory = new XmlSchemaFactory();
    private Optional<MessageObjectValidator> validator = Optional.empty();

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    public void setJaxbContext(Optional<JAXBContext> jaxbContext) {
        this.jaxbContext = jaxbContext;
    }

    @Override
    public byte[] toMessageData(Object object) throws SerializeException {
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (OutputStreamWriter osw = new OutputStreamWriter(bos, StandardCharsets.UTF_8)) {
            JAXBContext context = jaxbContext.orElseGet(() -> loadContextFor(object.getClass()));
            Marshaller marshaller = context.createMarshaller();
            Optional<Schema> schema = xmlSchemaFactory.schemaFor(object.getClass());
            schema.ifPresent(s -> marshaller.setSchema(s));
            marshaller.marshal(object, osw);
        } catch (IOException | JAXBException e) {
            throw new SerializeException("Could not serialize " + object + " to XML", e);
        }
        return bos.toByteArray();
    }

    private JAXBContext loadContextFor(Class<?> forType) {
        JAXBContext jaxb = contextsPerClass.get(forType);
        if (jaxb == null) {
            try {
                jaxb = JAXBContext.newInstance(forType);
                contextsPerClass.put(forType, jaxb);
            } catch (JAXBException e) {
                throw new SerializeException("Could not serialize " + forType + " to XML", e);
            }
        }
        return jaxb;
    }
}
