package org.kasource.spring.nats.integration.json;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.kasource.spring.nats.config.java.JsonSchemaConfiguration;
import org.kasource.spring.nats.config.java.NatsJackson;

import com.fasterxml.jackson.databind.ObjectMapper;


@Import({NatsJackson.class, JsonSchemaConfiguration.class})
@Configuration
public class NatsJacksonJavaConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
