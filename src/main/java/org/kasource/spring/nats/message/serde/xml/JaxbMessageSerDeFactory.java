package org.kasource.spring.nats.message.serde.xml;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JaxbMessageSerDeFactory implements NatsMessageSerDeFactory, ApplicationContextAware, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(JaxbMessageSerDeFactory.class);

    private JaxbMessageSerializer serializer = new JaxbMessageSerializer();
    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;
    private JAXBContext jaxbContext;
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        Optional<NatsMessageDeserializer> deserializer = Optional.ofNullable(deserializers.get(forClass));
        return deserializer.orElseGet(() -> createAndPut(forClass));
    }

    private NatsMessageDeserializer createAndPut(Class<?> forClass) {
        JaxbMessageDeserializer jaxbDeserializer =
                new JaxbMessageDeserializer(forClass, Optional.ofNullable(jaxbContext));

        jaxbDeserializer.setValidator(Optional.ofNullable(validator));
        deserializers.put(forClass, jaxbDeserializer);
        return jaxbDeserializer;
    }

    @Override
    public NatsMessageSerializer createSerializer() {
        return serializer;
    }

    @Override
    public void afterPropertiesSet() {
        if (jaxbContext == null) {
            try {
                jaxbContext = applicationContext.getBean(JAXBContext.class);
            } catch (BeansException e) {
                LOG.debug("No JAXBContext bean found, creating context at runtime");
            }
        }
        serializer.setJaxbContext(Optional.ofNullable(jaxbContext));
        serializer.setValidator(Optional.ofNullable(validator));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setValidator(MessageObjectValidator validator) {
        this.validator = validator;
    }

    public void setJaxbContext(JAXBContext jaxbContext) {
        this.jaxbContext = jaxbContext;
    }
}
