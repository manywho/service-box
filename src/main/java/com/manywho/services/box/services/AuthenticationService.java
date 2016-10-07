package com.manywho.services.box.services;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.enums.AuthenticationStatus;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.managers.CacheManagerInterface;

import javax.inject.Inject;

public class AuthenticationService {
    private BoxClient boxClient;
    private CacheManagerInterface cacheManager;

    @Inject
    public AuthenticationService(BoxClient boxClient, CacheManagerInterface cacheManager) {
        this.boxClient = boxClient;
        this.cacheManager = cacheManager;
    }

    public BoxAPIConnection confirmUserAuthenticationWithBox(String accessToken) {
        return boxClient.confirmUserAuthentication(accessToken);
    }

    public BoxAPIConnection authenticateUserWithBox(String clientId, String clientSecret, String code) {
        return boxClient.authenticateUser(clientId, clientSecret, code);
    }

    public AuthenticatedWhoResult buildAuthenticatedWhoResult(String providerName, String email, String name, String clientId, String id, String accessToken) {
        AuthenticatedWhoResult authenticatedWhoResult = new AuthenticatedWhoResult();
        authenticatedWhoResult.setDirectoryId(providerName);
        authenticatedWhoResult.setDirectoryName(providerName);
        authenticatedWhoResult.setEmail(email);
        authenticatedWhoResult.setFirstName(name);
        authenticatedWhoResult.setIdentityProvider(providerName);
        authenticatedWhoResult.setLastName(name);
        authenticatedWhoResult.setStatus(AuthenticationStatus.Authenticated);
        authenticatedWhoResult.setTenantName(clientId);
        authenticatedWhoResult.setToken(accessToken);
        authenticatedWhoResult.setUserId(id);
        authenticatedWhoResult.setUsername(email);

        return authenticatedWhoResult;
    }

    public BoxUser.Info getCurrentBoxUser(String accessToken) {
        return boxClient.getCurrentUser(accessToken);
    }

    public Credentials updateCredentials(String boxUserId) throws Exception {
        Credentials credentials = cacheManager.getCredentials(boxUserId);

        if(credentials!=  null) {
            BoxAPIConnection boxAPIConnection = boxClient.getValidBoxApiConnection(credentials);
            credentials.setAccessToken(boxAPIConnection.getAccessToken());

            if (boxAPIConnection.getRefreshToken() != null && !boxAPIConnection.getRefreshToken().isEmpty()) {
                credentials.setRefreshToken(boxAPIConnection.getRefreshToken());
            }

            cacheManager.saveCredentials(boxUserId, credentials);
        }

        return credentials;
    }
}
