package org.kasource.spring.nats.connection;

import org.springframework.core.io.Resource;

public class TlsConfiguration {
    private boolean enabled;
    private Resource trustStore;
    private String trustStorePassword;
    private Resource identityStore;
    private String identityStorePassword;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public Resource getTrustStore() {
        return trustStore;
    }

    public void setTrustStore(Resource trustStore) {
        this.trustStore = trustStore;
    }


    public String getTrustStorePassword() {
        return trustStorePassword;
    }

    public void setTrustStorePassword(String trustStorePassword) {
        this.trustStorePassword = trustStorePassword;
    }


    public Resource getIdentityStore() {
        return identityStore;
    }

    public void setIdentityStore(Resource identityStore) {
        this.identityStore = identityStore;
    }

    public String getIdentityStorePassword() {
        return identityStorePassword;
    }

    public void setIdentityStorePassword(String identityStorePassword) {
        this.identityStorePassword = identityStorePassword;
    }
}
