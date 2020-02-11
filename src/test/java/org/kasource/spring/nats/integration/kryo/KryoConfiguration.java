package org.kasource.spring.nats.integration.kryo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.kasource.spring.nats.config.java.NatsKryo;

@Import(NatsKryo.class)
@Configuration
public class KryoConfiguration {

    @Bean
    public ProjectListener projectListener() {
        return new ProjectListener();
    }
}
