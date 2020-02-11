package org.kasource.spring.nats.config;

@SuppressWarnings("PMD.ClassNamingConventions")
public final class NatsBeans {

    public static final String METER_REGISTRY = "natsMeterRegistry";
    public static final String NATS_TEMPLATE = "natsTemplate";
    public static final String CONSUMER_MANAGER = "natsConsumerManager";
    public static final String POST_BEAN_PROCESSOR = "natsPostBeanProcessor";
    public static final String NATS_CONNECTION_FACTORY = "natsConnectionFactory";
    public static final String SER_DE_FACTORY = "natsMessageSerDeFactory";
    public static final String JSON_SCHEMA_VALIDATOR_FACTORY_BEAN = "natsJsonSchemaValidatorFactoryBean";

    private NatsBeans() {
    }
}
