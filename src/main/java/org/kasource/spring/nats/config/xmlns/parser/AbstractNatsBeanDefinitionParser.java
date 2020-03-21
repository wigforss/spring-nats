package org.kasource.spring.nats.config.xmlns.parser;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;

import static org.kasource.spring.nats.config.NatsBeans.CONSUMER_MANAGER;
import static org.kasource.spring.nats.config.NatsBeans.METER_REGISTRY;
import static org.kasource.spring.nats.config.NatsBeans.NATS_CONNECTION_FACTORY;
import static org.kasource.spring.nats.config.NatsBeans.NATS_TEMPLATE;
import static org.kasource.spring.nats.config.NatsBeans.POST_BEAN_PROCESSOR;
import org.kasource.spring.nats.NatsTemplateImpl;
import org.kasource.spring.nats.config.xmlns.XmlNamespaceUtils;
import org.kasource.spring.nats.connection.ConnectionFactoryBean;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;
import org.kasource.spring.nats.consumer.NatsPostBeanProcessor;
import org.kasource.spring.nats.metrics.NatsMetricsRegistry;

import io.nats.client.Options;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;


public abstract class AbstractNatsBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

    protected boolean isAttributeTrue(Element element, String name, boolean defaltValue) {
        return getAttributeValue(element, name).map(s -> "true".equals(s)).orElse(defaltValue);
    }

    protected Optional<String> getAttributeValue(Element element, String name) {
        String value = element.getAttribute(name);
        if (StringUtils.isEmpty(value)) {
            return Optional.empty();
        } else {
            return Optional.of(value);
        }
    }

    protected void createBeans(ParserContext pc, Element element, String serDeFactoryBeanName) {
        Optional<String> connection = getAttributeValue(element, "connection");
        if (!connection.isPresent()) {
            createConnection(pc, element);
        }

        boolean enableMetrics = isAttributeTrue(element, "enable-metrics", true);
        Optional<String> meterRegistry = getAttributeValue(element, "meter-registry");

        if (enableMetrics && meterRegistry.isPresent()) {
            createNatsMetricsRegistry(pc, connection.orElse(NATS_CONNECTION_FACTORY), meterRegistry.get());
        }

        createNatsTemplate(pc, connection.orElse(NATS_CONNECTION_FACTORY), serDeFactoryBeanName);
        createConsumerManager(
                pc,
                connection.orElse(NATS_CONNECTION_FACTORY),
                serDeFactoryBeanName,
                isAttributeTrue(element, "auto-start-consumers", true)
        );
        createPostBeanProcessor(pc);
    }

    private void createConnection(ParserContext pc, Element element) {


        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(ConnectionFactoryBean.class);
        builder.setLazyInit(false);

        XmlNamespaceUtils.setValueIfPresent(
                "connection-url",
                "urls",
                element,
                builder,
                v -> Stream.of(StringUtils.split(v, ",")).map(u -> u.trim()).collect(Collectors.toList()));

        pc.registerBeanComponent(new BeanComponentDefinition(builder
                .getBeanDefinition(), NATS_CONNECTION_FACTORY));
    }


    private void createNatsMetricsRegistry(ParserContext pc, String connection, String meterRegistry) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(NatsMetricsRegistry.class);
        builder.setLazyInit(false);
        builder.addConstructorArgReference(meterRegistry);
        builder.addConstructorArgReference(connection);

        pc.registerBeanComponent(new BeanComponentDefinition(builder
                .getBeanDefinition(), METER_REGISTRY));

    }

    private void createPostBeanProcessor(ParserContext pc) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(NatsPostBeanProcessor.class);
        builder.setLazyInit(false);

        builder.addConstructorArgReference(CONSUMER_MANAGER);

        pc.registerBeanComponent(new BeanComponentDefinition(builder
                .getBeanDefinition(), POST_BEAN_PROCESSOR));
    }

    private void createNatsTemplate(ParserContext pc, String connection, String serDeFactory) {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(NatsTemplateImpl.class);
        builder.setLazyInit(false);

        builder.addConstructorArgReference(connection);
        builder.addConstructorArgReference(serDeFactory);

        pc.registerBeanComponent(new BeanComponentDefinition(builder
                .getBeanDefinition(), NATS_TEMPLATE));
    }

    private void createConsumerManager(ParserContext pc, String connection, String serDeFactory, boolean autoStart) {
        Optional<Long> drainTimeoutSeconds = resolveDrainTimeoutSeconds(pc);
        long drainTimeout = drainTimeoutSeconds.orElse(Options.DEFAULT_CONNECTION_TIMEOUT.getSeconds());
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(NatsConsumerManagerImpl.class);
        builder.setLazyInit(false);
        builder.addConstructorArgReference(connection);
        builder.addConstructorArgReference(serDeFactory);
        builder.addConstructorArgValue(drainTimeout);
        builder.addConstructorArgValue(autoStart);

        if (pc.getRegistry().containsBeanDefinition(METER_REGISTRY)) {
            builder.addPropertyReference("natsMetricsRegistry", METER_REGISTRY);
        }

        pc.registerBeanComponent(new BeanComponentDefinition(builder
                .getBeanDefinition(), CONSUMER_MANAGER));
    }


    private Optional<Long> resolveDrainTimeoutSeconds(ParserContext pc) {
        Optional<BeanDefinition> connectionFactoryBean = Arrays.stream(pc.getRegistry().getBeanDefinitionNames())
                .map(n -> pc.getRegistry().getBeanDefinition(n))
                .filter(b -> ConnectionFactoryBean.class.getName().equals(b.getBeanClassName())).findFirst();
        return connectionFactoryBean.map(b -> resolveDrainTimeout(b.getPropertyValues().getPropertyValue("drainTimeout")));

    }

    private Long resolveDrainTimeout(PropertyValue propertyValue) {
        if (propertyValue != null) {
            Object value = propertyValue.getValue();
            if (value != null && value instanceof Duration) {
                return ((Duration) value).getSeconds();
            }
        }
        return null;
    }
}
