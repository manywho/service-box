package com.manywho.services.box.services;

import com.manywho.services.box.configuration.EncryptConfiguration;
import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.jwk.PublicJsonWebKey;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;

public class EncryptService {
    private JsonWebEncryption senderJwe;
    private JsonWebEncryption receiverJwe;

    @Inject
    public EncryptService(EncryptConfiguration encryptConfiguration) {
        senderJwe = new JsonWebEncryption();
        receiverJwe = new JsonWebEncryption();

        try {
            PublicJsonWebKey key = PublicJsonWebKey.Factory.newPublicJwk(encryptConfiguration.getVerificationKey());
            senderJwe.setKey(key.getKey());
            receiverJwe.setKey(key.getPrivateKey());

        } catch (JoseException e) {
            throw new RuntimeException("Error generating verification.key", e);
        }

        senderJwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.ECDH_ES_A192KW);
        senderJwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_192_CBC_HMAC_SHA_384);
        receiverJwe.setAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, KeyManagementAlgorithmIdentifiers.ECDH_ES_A192KW));
        receiverJwe.setContentEncryptionAlgorithmConstraints(new AlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, ContentEncryptionAlgorithmIdentifiers.AES_192_CBC_HMAC_SHA_384));
    }

    public String encryptData(String input) throws JoseException {
        senderJwe.setPlaintext(input);

        return senderJwe.getCompactSerialization();
    }

    public String decryptData(String input) throws JoseException {
        receiverJwe.setCompactSerialization(input);

        return receiverJwe.getPlaintextString();
    }
}
