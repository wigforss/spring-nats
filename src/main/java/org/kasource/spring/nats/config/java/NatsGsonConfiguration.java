package org.kasource.spring.nats.config.java;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.json.NatsGsonMessageSerDeFactory;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.google.gson.Gson;

@Configuration
public class NatsGsonConfiguration {

    @Autowired
    private Gson gson;

    @Autowired
    private Optional<MessageObjectValidator> validator;

    @Bean(name = SER_DE_FACTORY)
    public NatsGsonMessageSerDeFactory natsGsonMessageSerDeFactory() {
        NatsGsonMessageSerDeFactory natsGsonMessageSerDeFactory = new NatsGsonMessageSerDeFactory();
        natsGsonMessageSerDeFactory.setGson(gson);
        validator.ifPresent(v -> natsGsonMessageSerDeFactory.setValidator(v));
        return natsGsonMessageSerDeFactory;
    }

}
