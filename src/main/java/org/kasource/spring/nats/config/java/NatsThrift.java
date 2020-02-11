package org.kasource.spring.nats.config.java;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({NatsThriftConfiguration.class, NatsConfiguration.class})
public class NatsThrift {
}
