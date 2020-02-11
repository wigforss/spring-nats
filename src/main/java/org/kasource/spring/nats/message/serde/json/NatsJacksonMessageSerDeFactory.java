package org.kasource.spring.nats.message.serde.json;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NatsJacksonMessageSerDeFactory implements NatsMessageSerDeFactory, ApplicationContextAware, InitializingBean {

    private ObjectMapper objectMapper;
    private NatsMessageSerializer serializer;
    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();

    private JsonSchemaValidator schemaValidator;
    private ApplicationContext applicationContext;
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        Optional<NatsMessageDeserializer> deserializer = Optional.ofNullable(deserializers.get(forClass));
        return deserializer.orElseGet(() -> createAndPut(forClass));
    }

    @Override
    public NatsMessageSerializer createSerializer() {
        return serializer;
    }

    @Override
    public void setValidator(MessageObjectValidator validator) {
        this.validator = validator;
    }

    private NatsMessageDeserializer createAndPut(Class<?> forClass) {
        NatsJacksonMessageDeserializer jacksonDeserializer =
                new NatsJacksonMessageDeserializer(objectMapper, forClass, Optional.ofNullable(schemaValidator));
        jacksonDeserializer.setValidator(Optional.ofNullable(validator));
        deserializers.put(forClass, jacksonDeserializer);
        return jacksonDeserializer;
    }

    @Override
    public void afterPropertiesSet() {
        if (objectMapper == null) {
            objectMapper = applicationContext.getBean(ObjectMapper.class);
        }
        serializer = new NatsJacksonMessageSerializer(objectMapper, Optional.ofNullable(schemaValidator));
        serializer.setValidator(Optional.ofNullable(validator));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setSchemaValidator(JsonSchemaValidator schemaValidator) {
        this.schemaValidator = schemaValidator;
    }
}
