package org.kasource.spring.nats.connection;

import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.lang.Nullable;

import org.kasource.spring.nats.connection.tls.SslContextBuilder;
import org.kasource.spring.nats.connection.tls.SslContextBuilderFactory;
import org.kasource.spring.nats.jwt.JwtAuth;

import io.nats.client.Connection;
import io.nats.client.ConnectionListener;
import io.nats.client.ErrorListener;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionFactoryBean extends AbstractFactoryBean<Connection> {
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionFactoryBean.class);

    private List<String> urls = List.of(Options.DEFAULT_URL);
    private int maxReconnects = Options.DEFAULT_MAX_RECONNECT;
    private Duration drainTimeout = Options.DEFAULT_CONNECTION_TIMEOUT;
    private String connectionName;
    private ErrorListener errorListener;
    private ConnectionListener connectionListener;
    private Duration connectionTimeout = Options.DEFAULT_CONNECTION_TIMEOUT;
    private String username;
    private String password;
    private TlsConfiguration tlsConfiguration = new TlsConfiguration();
    private SslContextBuilderFactory factory = new SslContextBuilderFactory();
    private String jwtToken;
    private String jwtNKey;



    private void configureCredentials(Options.Builder options) {
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {
            options.userInfo(username, password);
        } else if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            LOG.warn("Both username and password needs to be set, skipping since both is not set.");
        }

    }

    private void configureJwt(Options.Builder options) {
        if (!StringUtils.isEmpty(jwtToken) && !StringUtils.isEmpty(jwtNKey)) {
            options.authHandler(new JwtAuth(jwtToken, jwtNKey));
        } else if (!StringUtils.isEmpty(username) || !StringUtils.isEmpty(password)) {
            LOG.warn("Both token and n-key needs to be set, skipping since both is not set.");
        }

    }

    private void configureSSL(Options.Builder options) {

        try {
            if (tlsConfiguration.getTrustStore() == null) {
                options.opentls();
            } else {
                SslContextBuilder sslContextBuilder = factory.create(tlsConfiguration.getTrustStore(),
                        tlsConfiguration.getTrustStorePassword());
                if (tlsConfiguration.getIdentityStore() != null) {
                    sslContextBuilder.identityStore(tlsConfiguration.getIdentityStore(), tlsConfiguration.getIdentityStorePassword());
                }
                options.sslContext(sslContextBuilder.build());
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not enable tls", e);
        }

    }

    @Override
    public Class<?> getObjectType() {
        return Connection.class;
    }

    @Override
    protected Connection createInstance() throws Exception {
        Options.Builder options = new Options.Builder()
                .servers(urls.stream().toArray(String[]::new))
                .connectionTimeout(connectionTimeout)
                .maxReconnects(maxReconnects);

        if (!StringUtils.isEmpty(connectionName)) {
            options.connectionName(connectionName);
        }
        if (errorListener != null) {
            options.errorListener(errorListener);
        }
        if (connectionListener != null) {
            options.connectionListener(connectionListener);
        }
        configureCredentials(options);
        if (tlsConfiguration != null && tlsConfiguration.isEnabled()) {
            configureSSL(options);
        }
        configureJwt(options);
        return Nats.connect(options.build());
    }

    @Override
    protected void destroyInstance(@Nullable Connection instance) throws Exception {
        if (instance != null) {
            CompletableFuture<Boolean> drainFuture = instance.drain(drainTimeout);
            drainFuture.join();
            instance.close();
        }
    }

    @Override
    public boolean isSingleton() {
        return true;
    }


    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void setMaxReconnects(int maxReconnects) {
        this.maxReconnects = maxReconnects;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }

    public void setConnectionListener(ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void setConnectionTimeout(Duration connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTlsConfiguration(TlsConfiguration tlsConfiguration) {
        this.tlsConfiguration = tlsConfiguration;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public void setJwtNKey(String jwtNKey) {
        this.jwtNKey = jwtNKey;
    }

    public void setDrainTimeout(Duration drainTimeout) {
        this.drainTimeout = drainTimeout;
    }
}
