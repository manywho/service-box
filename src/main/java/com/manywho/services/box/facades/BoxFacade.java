package com.manywho.services.box.facades;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.EncryptionAlgorithm;
import com.box.sdk.JWTEncryptionPreferences;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.services.TokenCacheService;
import com.manywho.services.box.utilities.SystemInteractionInterface;

import javax.inject.Inject;
import java.io.IOException;

public class BoxFacade implements BoxFacadeInterface {
    private SecurityConfiguration securityConfiguration;
    private TokenCacheService tokenCacheService;
    private SystemInteractionInterface systemInteraction;

    @Inject
    public BoxFacade(SecurityConfiguration securityConfiguration, TokenCacheService tokenCacheService,
                     SystemInteractionInterface systemInteraction) {
        this.securityConfiguration = securityConfiguration;
        this.tokenCacheService = tokenCacheService;
        this.systemInteraction = systemInteraction;
    }

    @Override
    public BoxAPIConnection createApiConnection(String authorizationCode) {

        return new BoxAPIConnection(securityConfiguration.getOauth2ContentApiClientId(),
                securityConfiguration.getOauth2ContentApiClientSecret(), authorizationCode);
    }

    @Override
    public BoxAPIConnection createApiConnection(String accessToken, String refreshToken) {
        return new BoxAPIConnection(securityConfiguration.getOauth2ContentApiClientId(),
                securityConfiguration.getOauth2ContentApiClientSecret(), accessToken, refreshToken);
    }

    @Override
    public BoxDeveloperEditionAPIConnection createDeveloperApiConnection(String enterpriseId) {
        String privateKey;

        try {
            privateKey = systemInteraction.getFileContent(securityConfiguration.getPrivateKeyLocation());
        } catch (IOException e) {
            throw new RuntimeException("Error executing server to server connection");
        }

        JWTEncryptionPreferences encryptionPreferences = new JWTEncryptionPreferences();
        encryptionPreferences.setEncryptionAlgorithm(EncryptionAlgorithm.RSA_SHA_256);
        encryptionPreferences.setPrivateKey(privateKey);
        encryptionPreferences.setPrivateKeyPassword(securityConfiguration.getPrivateKeyPassword());

        return  BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(
                enterpriseId,
                securityConfiguration.getOauth2DeveloperEditionClientId(),
                securityConfiguration.getOauth2DeveloperEditionClientSecret(),
                encryptionPreferences,
                tokenCacheService.getAccessTokenCache()
        );
    }
}
