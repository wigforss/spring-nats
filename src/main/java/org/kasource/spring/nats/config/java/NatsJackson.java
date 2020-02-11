package org.kasource.spring.nats.config.java;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({NatsJacksonConfiguration.class, NatsConfiguration.class})
public class NatsJackson {
}
