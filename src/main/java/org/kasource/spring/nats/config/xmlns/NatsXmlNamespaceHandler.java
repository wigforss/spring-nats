package org.kasource.spring.nats.config.xmlns;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import org.kasource.spring.nats.config.xmlns.parser.ConnectionBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsAvroBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsCustomSerDeBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsGsonBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsJacksonBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsJavaBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsJaxbBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsKryoBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsProtobufBeanDefinitionParser;
import org.kasource.spring.nats.config.xmlns.parser.NatsThriftBeanDefinitionParser;

@SuppressWarnings("checkstyle:classdataabstractioncoupling")
public class NatsXmlNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        registerBeanDefinitionParser("connection", new ConnectionBeanDefinitionParser());
        registerBeanDefinitionParser("jackson", new NatsJacksonBeanDefinitionParser());
        registerBeanDefinitionParser("gson", new NatsGsonBeanDefinitionParser());
        registerBeanDefinitionParser("java", new NatsJavaBeanDefinitionParser());
        registerBeanDefinitionParser("protobuf", new NatsProtobufBeanDefinitionParser());
        registerBeanDefinitionParser("thrift", new NatsThriftBeanDefinitionParser());
        registerBeanDefinitionParser("avro", new NatsAvroBeanDefinitionParser());
        registerBeanDefinitionParser("jaxb", new NatsJaxbBeanDefinitionParser());
        registerBeanDefinitionParser("kryo", new NatsKryoBeanDefinitionParser());
        registerBeanDefinitionParser("custom", new NatsCustomSerDeBeanDefinitionParser());
    }
}
