package org.kasource.spring.nats.jwt;

import java.io.IOException;
import java.security.GeneralSecurityException;

import io.nats.client.AuthHandler;
import io.nats.client.NKey;

public class JwtAuth implements AuthHandler {

    private final char[] jwt;
    private final char[] nKeyChars;

    public JwtAuth(final String jwt, final String nKey) {
        this.jwt = jwt.toCharArray();
        this.nKeyChars = nKey.toCharArray();
    }


    @Override
    public byte[] sign(byte[] nonce) {
        try {
            return NKey.fromSeed(this.nKeyChars).sign(nonce);
        } catch (Exception e) {
            throw new IllegalStateException("Could not sign nonce", e);
        }
    }

    @Override
    public char[] getID() {
        try {
            return NKey.fromSeed(nKeyChars).getPublicKey();
        } catch (IOException | GeneralSecurityException e) {
            throw new IllegalStateException("Could not retrieve public key from NKey Seed", e);
        }
    }

    @Override
    public char[] getJWT() {
        char[] jwtCopy = new char[jwt.length];
        System.arraycopy(jwt, 0, jwtCopy, 0, jwt.length);
        return jwtCopy;
    }

}
