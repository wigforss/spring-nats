package org.kasource.spring.nats.config.xmlns.parser;

import java.util.Optional;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;

import static org.kasource.spring.nats.config.NatsBeans.SER_DE_FACTORY;
import org.kasource.spring.nats.config.xmlns.NatsCustomSerDe;

import org.w3c.dom.Element;

public class NatsCustomSerDeBeanDefinitionParser extends AbstractNatsBeanDefinitionParser {
    @Override
    protected Class<?> getBeanClass(Element element) {
        return NatsCustomSerDe.class;
    }

    @Override
    protected void doParse(Element element, ParserContext pc,
                           BeanDefinitionBuilder bean) {
        element.setAttribute(ID_ATTRIBUTE, SER_DE_FACTORY);
        element.setAttribute(NAME_ATTRIBUTE, SER_DE_FACTORY);
        Optional<String> serDeFactory = getAttributeValue(element, "message-serde-factory");
        if (!serDeFactory.isPresent()) {
            throw new IllegalStateException("Missing required attribute message-serde-factory");
        } else {
            createBeans(pc, element, serDeFactory.get());
        }
    }

}
