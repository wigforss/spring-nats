package org.kasource.spring.nats.integration.json;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.kasource.spring.nats.config.java.NatsGson;

import com.google.gson.Gson;

@Import(NatsGson.class)
@Configuration
public class NatsGsonJavaConfiguration {

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
