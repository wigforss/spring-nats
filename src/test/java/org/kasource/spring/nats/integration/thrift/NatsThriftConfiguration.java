package org.kasource.spring.nats.integration.thrift;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.kasource.spring.nats.config.java.NatsThrift;


@Configuration
@Import(NatsThrift.class)
public class NatsThriftConfiguration {

    @Bean
    public CrossPlatformListener crossPlatformListener() {
        return new CrossPlatformListener();
    }
}
