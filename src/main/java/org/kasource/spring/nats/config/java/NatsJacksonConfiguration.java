package org.kasource.spring.nats.config.java;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.json.NatsJacksonMessageSerDeFactory;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class NatsJacksonConfiguration {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Optional<MessageObjectValidator> validator;

    @Autowired
    private Optional<JsonSchemaValidator> jsonSchemaValidator;


    @Bean(name = SER_DE_FACTORY)
    public NatsJacksonMessageSerDeFactory natsJacksonMessageSerDeFactory() {
        NatsJacksonMessageSerDeFactory natsJacksonMessageSerDeFactory = new NatsJacksonMessageSerDeFactory();

        natsJacksonMessageSerDeFactory.setObjectMapper(objectMapper);
        jsonSchemaValidator.ifPresent(v -> natsJacksonMessageSerDeFactory.setSchemaValidator(v));
        validator.ifPresent(v -> natsJacksonMessageSerDeFactory.setValidator(v));

        return natsJacksonMessageSerDeFactory;
    }




}
