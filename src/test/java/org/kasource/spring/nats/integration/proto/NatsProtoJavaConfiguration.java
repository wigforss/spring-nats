package org.kasource.spring.nats.integration.proto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.kasource.spring.nats.config.java.NatsProtobuf;

@Import(NatsProtobuf.class)
@Configuration
public class NatsProtoJavaConfiguration {

    @Bean
    public SimpleMeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}
