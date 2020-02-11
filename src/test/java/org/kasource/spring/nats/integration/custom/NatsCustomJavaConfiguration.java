package org.kasource.spring.nats.integration.custom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.kasource.spring.nats.config.java.NatsConfiguration;

@Import(NatsConfiguration.class)
@Configuration
public class NatsCustomJavaConfiguration {
    @Bean
    public CustomSerDeFactory customSerDeFactory() {
        return new CustomSerDeFactory();
    }
}
