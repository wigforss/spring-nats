package org.kasource.spring.nats.message.validation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.kasource.json.schema.JsonSchemaScanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

public class JsonSchemaValidatorFactoryBean implements FactoryBean<JsonSchemaValidator>, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String packagesToScan;
    private ObjectMapper objectMapper;

    @Override
    public JsonSchemaValidator getObject() throws Exception {
        if (StringUtils.isEmpty(packagesToScan)) {
            throw new IllegalStateException("No packages are defined as base of scanning for @JsonSchema annotated classes."
                    + " Please configure base package(s) with json-schema-scan-packages.");
        }
        if (objectMapper == null) {
             objectMapper = applicationContext.getBean(ObjectMapper.class);
        }
        JsonSchemaScanner schemaScanner = new JsonSchemaScanner(objectMapper);
        List<String> basePackages = Stream.of(StringUtils.split(packagesToScan, ",")).map(p -> p.trim()).collect(Collectors.toList());
        String basePackage = basePackages.remove(0);
        return new JsonSchemaValidator(schemaScanner.scan(basePackage, basePackages.stream().toArray(String[]::new)));
    }

    @Override
    public Class<?> getObjectType() {
        return JsonSchemaValidator.class;
    }

    public void setPackagesToScan(String packagesToScan) {
        this.packagesToScan = packagesToScan;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
}
