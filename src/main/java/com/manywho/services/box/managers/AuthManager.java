package com.manywho.services.box.managers;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxUser;
import com.manywho.sdk.entities.UserObject;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
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
    private AuthenticationService authenticationService;
    private AuthorizationService authorizationService;
    private PropertyCollectionParser propertyParser;
    private CacheManagerInterface cacheManager;

    @Inject
    public AuthManager(AuthenticationService authenticationService, AuthorizationService authorizationService,
                       PropertyCollectionParser propertyParser,CacheManagerInterface cacheManager){
        this.authenticationService = authenticationService;
        this.propertyParser = propertyParser;
        this.authorizationService = authorizationService;
        this.cacheManager = cacheManager;
    }


    public AuthenticatedWhoResult authenticateUser(AbstractOauth2Provider provider, AuthenticationCredentials credentials) throws Exception {

        BoxAPIConnection apiConnection;
        Credentials credentialsBoxUser;
        BoxUser.Info userInformation;
        /**
         * if I receive a token then the user is authenticated and I need to confirm details
         * If I receive a code I need to get and access token and also save the refresh token
         */
        if(credentials.getToken()!= null) {
            apiConnection = authenticationService.confirmUserAuthenticationWithBox(credentials.getToken());
            userInformation = authenticationService.getCurrentBoxUser(apiConnection.getAccessToken());
        } else {
            apiConnection = authenticationService.authenticateUserWithBox(credentials.getCode());

            userInformation = authenticationService.getCurrentBoxUser(apiConnection.getAccessToken());
            cacheManager.saveCredentials(userInformation.getID(), new Credentials(apiConnection.getAccessToken(), apiConnection.getRefreshToken(), userInformation.getID()));
        }

        if (userInformation != null) {
            if(apiConnection.getRefreshToken() != null) {
                credentialsBoxUser = new Credentials(apiConnection.getAccessToken(), apiConnection.getRefreshToken(),
                        userInformation.getID());
            } else {
                credentialsBoxUser = cacheManager.getCredentials(userInformation.getID());
                credentialsBoxUser.setAccessToken(apiConnection.getAccessToken());
                cacheManager.saveCredentials(userInformation.getID(), credentialsBoxUser);
            }

            cacheManager.saveCredentials(userInformation.getID(), credentialsBoxUser);

            //cacheManager.saveUserIdByTokenKey(apiConnection.getAccessToken(), userInformation.getID());

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

        return new ObjectDataResponse(new ObjectCollection());
        //todo restore this line when we made the changes to be safe again and remove ignore from test
        //return new ObjectDataResponse(authorizationService.loadGroups(configuration.getEnterpriseId()));
    }

    public ObjectDataResponse loadGroupAttributes() {
        return new ObjectDataResponse(authorizationService.loadGroupAttributes());
    }

    public ObjectDataResponse loadUsers(ObjectDataRequest objectDataRequest) throws Exception {
        Configuration configuration = propertyParser.parse(objectDataRequest.getConfigurationValues(), Configuration.class);

        //todo restore this line when we made the changes to be safe again and remove ignore from test
        return new ObjectDataResponse(new ObjectCollection());
        //return new ObjectDataResponse(authorizationService.loadUsers(configuration.getEnterpriseId()));
    }

    public ObjectDataResponse loadUsersAttributes() {
        return new ObjectDataResponse(authorizationService.loadUsersAttributes());
    }
}
