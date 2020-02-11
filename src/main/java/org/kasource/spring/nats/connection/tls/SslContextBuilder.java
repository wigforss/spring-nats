package org.kasource.spring.nats.connection.tls;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.core.io.Resource;

import io.nats.client.Options;

public class SslContextBuilder {
    private TrustStoreManagerFactoryFactory tmff = new TrustStoreManagerFactoryFactory();
    private KeyManagerFactoryFactory kmff = new KeyManagerFactoryFactory();
    private Resource trustStore;
    private String trustStorePassword;
    private Resource identityStore;
    private String identityStorePassword;

    public SslContextBuilder(final Resource trustStore,
                             final String password) {
        this.trustStore = trustStore;
        this.trustStorePassword = password;
    }

    public SSLContext build() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance(Options.DEFAULT_SSL_PROTOCOL);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Could not create SSL Context", e);
        }
        initializeSSLContext(sslContext);
        return sslContext;
    }

    public SslContextBuilder identityStore(Resource keyStore, String password) {
        this.identityStore = keyStore;
        this.identityStorePassword = password;
        return this;
    }


    private void initializeSSLContext(SSLContext sslContext) {
        TrustManagerFactory tmf = tmff.create(trustStore, trustStorePassword);
        KeyManager[] km = null;
        if (identityStore != null) {
            KeyManagerFactory kmf = kmff.create(identityStore, identityStorePassword);
            km = kmf.getKeyManagers();
        }
        try {
                sslContext.init(km, tmf.getTrustManagers(), new SecureRandom());
        } catch (KeyManagementException e) {
            throw new IllegalStateException("Could not create SSL Context", e);
        }
    }


}
