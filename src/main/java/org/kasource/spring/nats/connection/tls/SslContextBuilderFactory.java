package org.kasource.spring.nats.connection.tls;

import org.springframework.core.io.Resource;

public class SslContextBuilderFactory {

    public SslContextBuilder create(Resource trustStore, String password) {
        return new SslContextBuilder(trustStore, password);
    }
}
