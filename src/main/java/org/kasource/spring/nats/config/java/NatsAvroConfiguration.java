package org.kasource.spring.nats.config.java;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.avro.AvroMessageSerDeFactory;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

@Configuration
public class NatsAvroConfiguration {

    @Autowired
    private Optional<MessageObjectValidator> validator;

    @Bean(name = SER_DE_FACTORY)
    public AvroMessageSerDeFactory avroMessageSerDeFactory() {
        AvroMessageSerDeFactory avroMessageSerDeFactory = new AvroMessageSerDeFactory();
        validator.ifPresent(v -> avroMessageSerDeFactory.setValidator(v));
        return avroMessageSerDeFactory;
    }
}
