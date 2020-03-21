package org.kasource.spring.nats.config.java;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import static org.kasource.spring.nats.config.NatsBeans.JSON_SCHEMA_VALIDATOR_FACTORY_BEAN;
import org.kasource.json.schema.JsonSchemaScanner;
import org.kasource.spring.nats.message.validation.JsonSchemaValidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

/**
 * Enable Optional JSON Schema validation for Jackson SerDe.
 * <p>
 * Should only be used together with @NatsJacksonConfiguration.
 */
@Configuration
public class JsonSchemaConfiguration {
    private static final String PROPERTY_JSON_SCHEMA_SCAN_PACKAGE = NatsConfiguration.PREFIX + "jackson.json-schema.scan-packages";

    @Autowired
    private Environment environment;

    @Autowired
    private ObjectMapper objectMapper;

    @Bean(name = JSON_SCHEMA_VALIDATOR_FACTORY_BEAN)
    public JsonSchemaValidator jsonSchemaValidator() {
        String packagesToScan = environment.getProperty(PROPERTY_JSON_SCHEMA_SCAN_PACKAGE, String.class);
        if (StringUtils.isEmpty(packagesToScan)) {
            throw new IllegalStateException("No packages are defined as base of scanning for @JsonSchema annotated classes."
                    + " Please configure base package(s) with the " + PROPERTY_JSON_SCHEMA_SCAN_PACKAGE + " property.");
        }
        JsonSchemaScanner discoverer = new JsonSchemaScanner(objectMapper);
        List<String> basePackages = Stream.of(StringUtils.split(packagesToScan, ",")).map(p -> p.trim()).collect(Collectors.toList());
        String basePackage = basePackages.remove(0);
        return new JsonSchemaValidator(discoverer.scan(basePackage, basePackages.stream().toArray(String[]::new)));
    }
}
