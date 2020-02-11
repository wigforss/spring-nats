package org.kasource.spring.nats.connection.tls;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;

import org.springframework.core.io.Resource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class KeyManagerFactoryFactory {

    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    public KeyManagerFactory create(Resource identityStoreResource, String password) {

        try (InputStream is = identityStoreResource.getInputStream()) {
            KeyStore identityStore = KeyStore.getInstance(KeyStore.getDefaultType());
            identityStore.load(is, password.toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
            kmf.init(identityStore, password.toCharArray());
            return kmf;
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Could not load identity-store from " + identityStoreResource, e);
        }
    }
}
