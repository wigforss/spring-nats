package org.kasource.spring.nats.config.xmlns.parser;

import java.util.Optional;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import static org.kasource.spring.nats.config.NatsBeans.JSON_SCHEMA_VALIDATOR_FACTORY_BEAN;
import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.json.NatsJacksonMessageSerDeFactory;
import org.kasource.spring.nats.message.validation.JsonSchemaValidatorFactoryBean;

import org.w3c.dom.Element;

public class NatsJacksonBeanDefinitionParser extends AbstractNatsBeanDefinitionParser {


    @Override
    protected Class<?> getBeanClass(Element element) {
        return NatsJacksonMessageSerDeFactory.class;
    }

    @Override
    protected void doParse(Element element, ParserContext pc,
                           BeanDefinitionBuilder bean) {
        element.setAttribute(ID_ATTRIBUTE, SER_DE_FACTORY);
        element.setAttribute(NAME_ATTRIBUTE, SER_DE_FACTORY);
        Optional<String> objectValidator = getAttributeValue(element, "object-validator");
        objectValidator.ifPresent(v -> bean.addPropertyReference("validator", v));

        Optional<String> objectMapperBean = getAttributeValue(element, "object-mapper");
        objectMapperBean.ifPresent(n -> bean.addPropertyReference("objectMapper", n));

        Optional<String> jsonSchemaScanPackages = getAttributeValue(element, "json-schema-scan-packages");
        if (jsonSchemaScanPackages.isPresent()) {

            if (!hasClass("org.kasource.json.schema.JsonSchemaScanner")) {
                throw new IllegalStateException("Missing class org.kasource.json.schema.JsonSchemaScanner add "
                        + "org.kasource:json-schema-registry as a dependency.");
            }
            createJsonSchemaDataValidator(pc, jsonSchemaScanPackages.get(), objectMapperBean);
            bean.addPropertyReference("schemaValidator", JSON_SCHEMA_VALIDATOR_FACTORY_BEAN);
        }
        createBeans(pc, element, SER_DE_FACTORY);
    }

    private void createJsonSchemaDataValidator(ParserContext pc, String jsonSchemaScanPackages, Optional<String> objectMapper) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(JsonSchemaValidatorFactoryBean.class);
        builder.setLazyInit(false);
        builder.addPropertyValue("packagesToScan", jsonSchemaScanPackages);
        objectMapper.ifPresent(om -> builder.addPropertyReference("objectMapper", om));
        pc.registerBeanComponent(new BeanComponentDefinition(builder
                .getBeanDefinition(), JSON_SCHEMA_VALIDATOR_FACTORY_BEAN));
    }

    protected boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
