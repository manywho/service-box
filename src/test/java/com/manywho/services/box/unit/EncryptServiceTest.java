package com.manywho.services.box.unit;

import com.manywho.services.box.configuration.EncryptConfiguration;
import com.manywho.services.box.services.EncryptService;
import org.jose4j.jwk.EcJwkGenerator;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.keys.EllipticCurves;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class EncryptServiceTest {
    @Test
    public void encryptAndDecryptTest() throws Exception {

        EncryptConfiguration encryptConfiguration = Mockito.mock(EncryptConfiguration.class);
        JsonWebKey key = EcJwkGenerator.generateJwk(EllipticCurves.P256);
        String verificationKey = key.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE);

        Mockito.when(encryptConfiguration.getVerificationKey()).thenReturn(verificationKey);

        EncryptService encryptService = new EncryptService(encryptConfiguration);
        String valueToEncrypt = "value to encrypt %£\"!\"£{}@~?>,||\\";

        String encrypted1 = encryptService.encryptData(valueToEncrypt);
        String decryptedValue1 = encryptService.decryptData(encrypted1);
        Assert.assertEquals(valueToEncrypt, decryptedValue1);

        String encrypted2 = encryptService.encryptData(valueToEncrypt);

        // the encryption result have to be different each time, even for the same value
        Assert.assertNotEquals(encrypted1, encrypted2);

        String decryptedValue2 = encryptService.decryptData(encrypted2);
        Assert.assertEquals(decryptedValue1, decryptedValue2);
    }
}
