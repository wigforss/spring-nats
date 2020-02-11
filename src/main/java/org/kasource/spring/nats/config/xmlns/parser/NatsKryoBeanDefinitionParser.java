package org.kasource.spring.nats.config.xmlns.parser;

import java.util.Optional;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.message.serde.kryo.NatsKryoSerDeFactory;

import org.w3c.dom.Element;

public class NatsKryoBeanDefinitionParser extends AbstractNatsBeanDefinitionParser {

    @Override
    protected Class<?> getBeanClass(Element element) {
        return NatsKryoSerDeFactory.class;
    }

    @Override
    protected void doParse(Element element, ParserContext pc,
                           BeanDefinitionBuilder bean) {
        element.setAttribute(ID_ATTRIBUTE, SER_DE_FACTORY);
        element.setAttribute(NAME_ATTRIBUTE, SER_DE_FACTORY);

        Optional<String> kryoFactory = getAttributeValue(element, "kryo-factory");

        kryoFactory.ifPresent(k -> bean.addPropertyReference("kryoFactory", k));
        Optional<String> objectValidator = getAttributeValue(element, "object-validator");
        objectValidator.ifPresent(v -> bean.addPropertyReference("validator", v));

        createBeans(pc, element, SER_DE_FACTORY);
    }

}
