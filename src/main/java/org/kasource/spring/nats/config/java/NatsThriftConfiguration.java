package org.kasource.spring.nats.config.java;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.thrift.ThriftMessageSerDeFactory;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

@Configuration
public class NatsThriftConfiguration {

    @Autowired
    private Optional<MessageObjectValidator> validator;

    @Bean(name = SER_DE_FACTORY)
    public ThriftMessageSerDeFactory thriftMessageSerDeFactory() {
        ThriftMessageSerDeFactory thriftMessageSerDeFactory = new ThriftMessageSerDeFactory();
        validator.ifPresent(v -> thriftMessageSerDeFactory.setValidator(v));
        return thriftMessageSerDeFactory;
    }
}
