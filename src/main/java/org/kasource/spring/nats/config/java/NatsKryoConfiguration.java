package org.kasource.spring.nats.config.java;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.kryo.KryoFactory;
import org.kasource.spring.nats.message.serde.kryo.NatsKryoSerDeFactory;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

@Configuration
public class NatsKryoConfiguration {

    @Autowired
    private Optional<KryoFactory> kryoFactory;

    @Autowired
    private Optional<MessageObjectValidator> validator;


    @Bean(name = SER_DE_FACTORY)
    public NatsKryoSerDeFactory natsKryoMessageSerDeFactory() {
        NatsKryoSerDeFactory natsKryoMessageSerDeFactory = new NatsKryoSerDeFactory();
        kryoFactory.ifPresent(k -> natsKryoMessageSerDeFactory.setKryoFactory(k));
        validator.ifPresent(v -> natsKryoMessageSerDeFactory.setValidator(v));
        return natsKryoMessageSerDeFactory;
    }
}
