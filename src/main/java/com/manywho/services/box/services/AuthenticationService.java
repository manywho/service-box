package com.manywho.services.box.services;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.enums.AuthenticationStatus;
import com.manywho.services.box.facades.BoxFacade;

import javax.inject.Inject;

public class AuthenticationService {

    @Inject
    private BoxFacade boxFacade;

    public BoxAPIConnection authenticateUserWithBox(String clientId, String clientSecret, String code) {
        return boxFacade.authenticateUser(clientId, clientSecret, code);
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
        return boxFacade.getCurrentUser(accessToken);
    }
}
