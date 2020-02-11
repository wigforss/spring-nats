package org.kasource.spring.nats.integration.avro;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.nats.client.Connection;
import org.kasource.spring.nats.config.java.NatsAvro;
import org.kasource.spring.nats.event.NatsErrorListener;
import org.kasource.spring.nats.metrics.NatsMetricsRegistry;

@Import(NatsAvro.class)
@Configuration
public class NatsAvroJavaConfiguration {

    @Bean
    public NatsErrorListener errorListener() {
        return new NatsErrorListener();
    }

    @Bean
    public SimpleMeterRegistry simpleMeterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public NatsMetricsRegistry natsMetricsRegistry(Connection connection) {
        return new NatsMetricsRegistry(simpleMeterRegistry(), connection);
    }
}
