package com.manywho.services.box.services;

import com.manywho.services.box.configuration.EncryptConfiguration;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;
import java.security.Key;

public class EncryptService {
    private Key key;
    private AlgorithmConstraints algorithmKeyConstraints;
    private AlgorithmConstraints algorithmContentConstraints;

    @Inject
    public EncryptService(EncryptConfiguration encryptConfiguration) {
        key = new AesKey(encryptConfiguration.getInitializationInteger().getBytes());

        algorithmKeyConstraints = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                KeyManagementAlgorithmIdentifiers.A128KW);

        algorithmContentConstraints = new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST,
                ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
    }

    public String encryptData(String input) throws JoseException {
        JsonWebEncryption jwe = new JsonWebEncryption();
        jwe.setPayload(input);
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);

        return jwe.getCompactSerialization();
    }

    public String decryptData(String input) throws JoseException {
        JsonWebEncryption jwe = new JsonWebEncryption();

        jwe.setAlgorithmConstraints(algorithmKeyConstraints);
        jwe.setContentEncryptionAlgorithmConstraints(algorithmContentConstraints);
        jwe.setCompactSerialization(input);
        jwe.setKey(key);

        return jwe.getPayload();
    }
}
