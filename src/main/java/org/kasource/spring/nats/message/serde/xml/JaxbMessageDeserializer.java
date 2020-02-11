package org.kasource.spring.nats.message.serde.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;

public class JaxbMessageDeserializer implements NatsMessageDeserializer {

    private JAXBContext jaxbContext;
    private Optional<Schema> schema;
    private XmlSchemaFactory xmlSchemaFactory = new XmlSchemaFactory();
    private Optional<MessageObjectValidator> validator = Optional.empty();


    public JaxbMessageDeserializer(final Class<?> forType, final Optional<JAXBContext> jaxbContext) {
        this.jaxbContext = jaxbContext.orElse(loadContextFor(forType));
        this.schema = xmlSchemaFactory.schemaFor(forType);
    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    private JAXBContext loadContextFor(Class<?> forType) {
        try {
            return JAXBContext.newInstance(forType);
        } catch (JAXBException e) {
            throw new IllegalArgumentException("Could not create JAXB Context for " + forType, e);
        }
    }

    @Override
    public Object fromMessage(Message message) throws DeserializeException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(message.getData())) {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            schema.ifPresent(s -> unmarshaller.setSchema(s));
            Object object = unmarshaller.unmarshal(bis);
            validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
            return object;
        } catch (IOException | JAXBException e) {
            throw new DeserializeException("Could not de-serialize message " + message, e);
        }
    }
}
