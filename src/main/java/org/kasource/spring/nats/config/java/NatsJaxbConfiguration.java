package org.kasource.spring.nats.config.java;

import java.util.Optional;

import javax.xml.bind.JAXBContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.xml.JaxbMessageSerDeFactory;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

@Configuration
public class NatsJaxbConfiguration {

    @Autowired
    private Optional<MessageObjectValidator> validator;

    @Autowired
    private Optional<JAXBContext> jaxbContext;

    @Bean(name = SER_DE_FACTORY)
    public JaxbMessageSerDeFactory jaxbMessageSerDeFactory() {
        JaxbMessageSerDeFactory jaxbMessageSerDeFactory = new JaxbMessageSerDeFactory();
        validator.ifPresent(v -> jaxbMessageSerDeFactory.setValidator(v));
        jaxbContext.ifPresent(c -> jaxbMessageSerDeFactory.setJaxbContext(c));
        return jaxbMessageSerDeFactory;
    }
}
