package org.kasource.spring.nats.config.java;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import static org.kasource.spring.nats.config.NatsBeans.CONSUMER_MANAGER;
import static org.kasource.spring.nats.config.NatsBeans.NATS_CONNECTION_FACTORY;
import static org.kasource.spring.nats.config.NatsBeans.NATS_TEMPLATE;
import static org.kasource.spring.nats.config.NatsBeans.POST_BEAN_PROCESSOR;
import org.kasource.spring.nats.NatsTemplate;
import org.kasource.spring.nats.NatsTemplateImpl;
import org.kasource.spring.nats.connection.ConnectionFactoryBean;
import org.kasource.spring.nats.connection.TlsConfiguration;
import org.kasource.spring.nats.consumer.NatsConsumerManager;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;
import org.kasource.spring.nats.consumer.NatsPostBeanProcessor;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.ErrorListener;
import io.nats.client.Options;
import org.apache.commons.lang.StringUtils;


@Configuration
public class NatsConfiguration {

    public static final String PREFIX = "spring.nats.";
    private static final String PROPERTY_AUTO_START_CONSUMERS = PREFIX + "auto-start-consumers";
    private static final String CONNECTION_PREFIX = PREFIX + "connection.";
    private static final String PROPERTY_MAX_RECONNECTS = CONNECTION_PREFIX + "max-reconnects";
    private static final String PROPERTY_URL = CONNECTION_PREFIX + "url";
    private static final String PROPERTY_CONNECTION_NAME = CONNECTION_PREFIX + "name";
    private static final String PROPERTY_TIMEOUT_SECONDS = CONNECTION_PREFIX + "timeout-seconds";
    private static final String PROPERTY_USERNAME = CONNECTION_PREFIX + "username";
    private static final String PROPERTY_PASSWORD = CONNECTION_PREFIX + "password";
    private static final String PROPERTY_JWT_TOKEN = CONNECTION_PREFIX + "jwt.token";
    private static final String PROPERTY_JWT_N_KEY = CONNECTION_PREFIX + "jwt.n-key";
    private static final String DRAIN_TIMEOUT = CONNECTION_PREFIX + "drain-timeout-seconds";


    private static final String TLS_PREFIX = CONNECTION_PREFIX + "tls.";
    private static final String PROPERTY_TLS_ENABLED = TLS_PREFIX + "enabled";
    private static final String PROPERTY_TLS_TRUST_STORE = TLS_PREFIX + "trust-store";
    private static final String PROPERTY_TLS_TRUST_STORE_PASSWORD = TLS_PREFIX + "trust-store-password";
    private static final String PROPERTY_TLS_IDENTITY_STORE = TLS_PREFIX + "identity-store";
    private static final String PROPERTY_TLS_IDENTITY_STORE_PASSWORD = TLS_PREFIX + "identity-store-password";

    @Autowired
    private Environment environment;

    @Autowired
    private Optional<ErrorListener> errorListener;

    @Autowired
    private Optional<ConnectionListener> connectionListener;

    @Autowired
    private ResourceLoader resourceLoader;

    @Bean(name = NATS_CONNECTION_FACTORY)
    public ConnectionFactoryBean natsConnectionFactoryBean() {
        ConnectionFactoryBean factoryBean = new ConnectionFactoryBean();
        Optional<String> url = getProperty(PROPERTY_URL, String.class);
        if (url.map(u -> !StringUtils.isEmpty(u)).orElse(false)) {
           List<String> urls = Stream.of(StringUtils.split(url.get(), ",")).map(u -> u.trim()).collect(Collectors.toList());
           factoryBean.setUrls(urls);
        }
        getProperty(PROPERTY_MAX_RECONNECTS, Integer.class).ifPresent(r -> factoryBean.setMaxReconnects(r));
        getProperty(PROPERTY_CONNECTION_NAME, String.class).ifPresent(n -> factoryBean.setConnectionName(n));
        getProperty(PROPERTY_TIMEOUT_SECONDS, Integer.class).ifPresent(t -> factoryBean.setConnectionTimeout(Duration.ofSeconds(t)));
        getProperty(PROPERTY_USERNAME, String.class).ifPresent(u -> factoryBean.setUsername(u));
        getProperty(PROPERTY_PASSWORD, String.class).ifPresent(p -> factoryBean.setPassword(p));
        getProperty(PROPERTY_JWT_TOKEN, String.class).ifPresent(t -> factoryBean.setJwtToken(t));
        getProperty(PROPERTY_JWT_N_KEY, String.class).ifPresent(k -> factoryBean.setJwtNKey(k));
        factoryBean.setTlsConfiguration(parseTlsConfiguration());
        errorListener.ifPresent(e -> factoryBean.setErrorListener(e));
        connectionListener.ifPresent(c -> factoryBean.setConnectionListener(c));
        return factoryBean;
    }



    private TlsConfiguration parseTlsConfiguration() {
        TlsConfiguration tlsConfiguration = new TlsConfiguration();
        if (getProperty(PROPERTY_TLS_ENABLED, Boolean.class).orElse(false)) {
            tlsConfiguration.setEnabled(true);
            getProperty(PROPERTY_TLS_TRUST_STORE, String.class)
                    .ifPresent(t -> tlsConfiguration.setTrustStore(resourceLoader.getResource(t)));
            getProperty(PROPERTY_TLS_TRUST_STORE_PASSWORD, String.class)
                    .ifPresent(p -> tlsConfiguration.setTrustStorePassword(p));
            getProperty(PROPERTY_TLS_IDENTITY_STORE, String.class)
                    .ifPresent(i -> tlsConfiguration.setIdentityStore(resourceLoader.getResource(i)));
            getProperty(PROPERTY_TLS_IDENTITY_STORE_PASSWORD, String.class)
                    .ifPresent(p -> tlsConfiguration.setIdentityStorePassword(p));
        }
        return tlsConfiguration;
    }

    @Bean(name = NATS_TEMPLATE)
    public NatsTemplate natsTemplate(Connection connection, NatsMessageSerDeFactory serDeFactory) {
        return new NatsTemplateImpl(connection, serDeFactory);
    }

    @Bean(name = CONSUMER_MANAGER)
    public NatsConsumerManager natsConsumerManager(Connection connection, NatsMessageSerDeFactory serDeFactory) {
        long drainTimeoutSeconds = getProperty(DRAIN_TIMEOUT, Long.class).orElse(Options.DEFAULT_CONNECTION_TIMEOUT.getSeconds());
        boolean autoStart = isPropertyTrue(PROPERTY_AUTO_START_CONSUMERS, true);
        return new NatsConsumerManagerImpl(connection, serDeFactory, drainTimeoutSeconds, autoStart);
    }

    @Bean(name = POST_BEAN_PROCESSOR)
    public NatsPostBeanProcessor natsPostBeanProcessor(NatsConsumerManager consumerManager) {
        return new NatsPostBeanProcessor(consumerManager);
    }

    private boolean isPropertyTrue(String name, boolean defaultValue) {
        return getProperty(name, Boolean.class).orElse(defaultValue);
    }

    private <T> Optional<T> getProperty(String name, Class<T> ofType) {
        return Optional.ofNullable(environment.getProperty(name, ofType));
    }
}
