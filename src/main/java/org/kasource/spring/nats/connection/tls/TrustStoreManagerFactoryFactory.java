package org.kasource.spring.nats.connection.tls;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.TrustManagerFactory;

import org.springframework.core.io.Resource;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class TrustStoreManagerFactoryFactory {
    @SuppressFBWarnings("RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
    public TrustManagerFactory create(Resource trustStoreResource, String password) {
        try (InputStream is = trustStoreResource.getInputStream()) {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(is, password.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
            tmf.init(trustStore);
            return tmf;
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Could not load trust-store from " + trustStoreResource, e);
        }
    }


}
