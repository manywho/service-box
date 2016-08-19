package com.manywho.services.box.managers;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.UserObject;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.enums.AuthorizationType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.entities.Configuration;
import com.manywho.services.box.entities.Credentials;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.AuthorizationService;
import org.scribe.oauth.OAuthService;

import javax.inject.Inject;

public class AuthManager {
    @Inject
    private AuthenticationService authenticationService;

    @Inject
    private AuthorizationService authorizationService;

    @Inject
    private PropertyCollectionParser propertyParser;

    @Inject
    private CacheManager cacheManager;

    public AuthenticatedWhoResult authenticateUser(AbstractOauth2Provider provider, AuthenticationCredentials credentials) throws Exception {
        BoxAPIConnection apiConnection = authenticationService.authenticateUserWithBox(
                provider.getClientId(),
                provider.getClientSecret(),
                credentials.getCode()
        );

        BoxUser.Info userInformation = authenticationService.getCurrentBoxUser(apiConnection.getAccessToken());
        if (userInformation != null) {
            Credentials credentialsBoxUser = new Credentials(apiConnection.getAccessToken(), apiConnection.getRefreshToken());
            cacheManager.saveCredentails(userInformation.getID(), credentialsBoxUser);

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

    public ObjectDataResponse authorizeUser(OAuthService oauthService, AbstractOauth2Provider provider, AuthenticatedWho user, ObjectDataRequest objectDataRequest) {
        // Check if the logged-in user is authorized for this flow
        String authorizationStatus = authorizationService.getUserAuthorizationStatus(objectDataRequest.getAuthorization(), user);

        UserObject userObject = new UserObject(
                provider.getName(),
                AuthorizationType.Oauth2,
                oauthService.getAuthorizationUrl(null),
                authorizationStatus
        );

        return new ObjectDataResponse(userObject);
    }

    public ObjectDataResponse loadGroups(ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);

        return new ObjectDataResponse(authorizationService.loadGroups(configuration.getEnterpriseId()));
    }

    public ObjectDataResponse loadGroupAttributes() {
        return new ObjectDataResponse(authorizationService.loadGroupAttributes());
    }

    public ObjectDataResponse loadUsers(ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);

        return new ObjectDataResponse(authorizationService.loadUsers(configuration.getEnterpriseId()));
    }
}
