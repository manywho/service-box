package com.manywho.services.box.managers;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.UserObject;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.enums.AuthorizationType;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.services.AuthenticationService;
import org.scribe.oauth.OAuthService;

import javax.inject.Inject;

public class AuthManager {
    @Inject
    private AuthenticationService authenticationService;

    public AuthenticatedWhoResult authenticateUser(AbstractOauth2Provider provider, AuthenticationCredentials credentials) throws Exception {
        BoxAPIConnection apiConnection = authenticationService.authenticateUserWithBox(
                provider.getClientId(),
                provider.getClientSecret(),
                credentials.getCode()
        );

        BoxUser.Info userInformation = authenticationService.getCurrentBoxUser(apiConnection.getAccessToken());
        if (userInformation != null) {
            return authenticationService.buildAuthenticatedWhoResult(
                    provider.getName(),
                    userInformation.getLogin(),
                    userInformation.getName(),
                    provider.getClientId(),
                    userInformation.getID(),
                    apiConnection.getAccessToken()
            );
        }

        throw new Exception("Unable to authenticate with Box");
    }

    public ObjectDataResponse authorizeUser(OAuthService oauthService, AbstractOauth2Provider provider, String authorizationStatus) {
        UserObject userObject = new UserObject(
                provider.getName(),
                AuthorizationType.Oauth2,
                oauthService.getAuthorizationUrl(null),
                authorizationStatus
        );

        return new ObjectDataResponse(userObject);
    }
}
