package org.kasource.spring.nats.config.xmlns.parser;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;

import static org.kasource.spring.nats.config.NatsBeans.NATS_CONNECTION_FACTORY;
import org.kasource.spring.nats.config.xmlns.XmlNamespaceUtils;
import org.kasource.spring.nats.connection.ConnectionFactoryBean;
import org.kasource.spring.nats.connection.TlsConfiguration;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
                    justification = "Spring provides the resourceLoader")
public class ConnectionBeanDefinitionParser extends AbstractSingleBeanDefinitionParser
        implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    @Override
    protected Class<?> getBeanClass(Element element) {
        return ConnectionFactoryBean.class;
    }

    /**
     * parse the connection XML element.
     *
     * @param element connection XML element.
     * @param pc      Parser context.
     * @param bean    Bean definition.
     **/
    @Override
    protected void doParse(Element element, ParserContext pc,
                           BeanDefinitionBuilder bean) {

        String idAttribute = element.getAttribute(ID_ATTRIBUTE);
        if (StringUtils.isEmpty(idAttribute)) {
            element.setAttribute(ID_ATTRIBUTE, NATS_CONNECTION_FACTORY);
            element.setAttribute(NAME_ATTRIBUTE, NATS_CONNECTION_FACTORY);
        } else {
            element.setAttribute(NAME_ATTRIBUTE, idAttribute);
        }
        XmlNamespaceUtils.setValueIfPresent(
                "urls",
                element,
                bean,
                v -> Stream.of(StringUtils.split(v, ",")).map(u -> u.trim()).collect(Collectors.toList()));


        XmlNamespaceUtils.setValueIfPresent(
                "max-reconnects",
                element,
                bean,
                v -> Long.parseLong(v));

        XmlNamespaceUtils.setReferenceIfPresent(
                "connection-listener",
                element,
                bean);

        XmlNamespaceUtils.setReferenceIfPresent(
                "error-listener",
                element,
                bean);

        XmlNamespaceUtils.setValueIfPresent(
                "connection-timeout-seconds",
                "connectionTimeout",
                element,
                bean, v -> Duration.ofSeconds(Long.parseLong(v)));

        XmlNamespaceUtils.setValueIfPresent(
                "drain-timeout-seconds",
                "drainTimeout",
                element,
                bean,
                v -> Duration.ofSeconds(Long.parseLong(v)));

        bean.addPropertyValue("username", element.getAttribute("username"));
        bean.addPropertyValue("password", element.getAttribute("password"));

        bean.addPropertyValue("tlsConfiguration", parseTlsConfiguration(element));
        bean.addPropertyValue("jwtToken", parseJwtToken(element));
        bean.addPropertyValue("jwtNKey", parseJwtKey(element));
        bean.setLazyInit(false);

    }


    private TlsConfiguration parseTlsConfiguration(Element element) {
        TlsConfiguration tlsConfiguration = new TlsConfiguration();
        NodeList tlsList = element.getElementsByTagName("tls");
        if (tlsList.getLength() == 1) {
           Node tls =  tlsList.item(0);
           tlsConfiguration.setEnabled(getAttributeValue(tls, "enabled").map(v -> Boolean.valueOf(v)).orElse(false));
           tlsConfiguration.setTrustStore(getAttributeValue(tls, "trust-store").map(r -> resourceLoader.getResource(r)).orElse(null));
           tlsConfiguration.setTrustStorePassword(getAttributeValue(tls, "trust-store-password").orElse(null));
           tlsConfiguration.setIdentityStore(getAttributeValue(tls, "identity-store").map(r -> resourceLoader.getResource(r)).orElse(null));
           tlsConfiguration.setIdentityStorePassword(getAttributeValue(tls, "identity-store-password").orElse(null));
        }

        return tlsConfiguration;
    }

    private String parseJwtToken(Element element) {
        NodeList list = element.getElementsByTagName("jwt");
        if (list.getLength() == 1) {
            return getAttributeValue(list.item(0), "token").orElse(null);
        }
        return null;
    }

    private String parseJwtKey(Element element) {
        NodeList list = element.getElementsByTagName("jwt");
        if (list.getLength() == 1) {
            return getAttributeValue(list.item(0), "n-key").orElse(null);
        }
        return null;
    }

    private Optional<String> getAttributeValue(Node node, String name) {
        return Optional.ofNullable(node.getAttributes().getNamedItem(name)).map(a -> a.getNodeValue());


    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
