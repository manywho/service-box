package com.manywho.services.box.facades;

import com.box.sdk.*;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.services.TokenCacheService;
import com.manywho.services.box.utilities.SystemInteractionInterface;

import javax.inject.Inject;

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

        BoxConfig boxConfig = new BoxConfig(
                securityConfiguration.getOauth2DeveloperEditionClientId(),
                securityConfiguration.getOauth2DeveloperEditionClientSecret(),
                enterpriseId,
                securityConfiguration.getOauth2DeveloperEditionPublicId(),
                securityConfiguration.getPrivateKey(),
                securityConfiguration.getPrivateKeyPassword()
        );

        return  BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(boxConfig, tokenCacheService.getAccessTokenCache());
    }
}
