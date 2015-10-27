package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWhoResult;
import com.manywho.sdk.entities.security.AuthenticationCredentials;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractOauth2Controller;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.managers.AuthManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/")
@Consumes("application/json")
@Produces("application/json")
public class AuthController extends AbstractOauth2Controller {
    @Inject
    private AuthManager authManager;

    @Inject
    public AuthController(AbstractOauth2Provider oauth2Provider) {
        super(oauth2Provider);
    }

    @Override
    public AuthenticatedWhoResult authentication(AuthenticationCredentials authenticationCredentials) throws Exception {
        return authManager.authenticateUser(oauth2Provider, authenticationCredentials);
    }

    @Path("/authorization")
    @POST
    @AuthorizationRequired
    public ObjectDataResponse authorization(ObjectDataRequest objectDataRequest) throws Exception {
        return authManager.authorizeUser(getOauthService(), oauth2Provider, getAuthenticatedWho(), objectDataRequest);
    }

    @Override
    public ObjectDataResponse groups(ObjectDataRequest objectDataRequest) throws Exception {
        return authManager.loadGroups(objectDataRequest);
    }

    @Override
    public ObjectDataResponse groupAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        return authManager.loadGroupAttributes();
    }

    @Override
    public ObjectDataResponse users(ObjectDataRequest objectDataRequest) throws Exception {
        return authManager.loadUsers(objectDataRequest);
    }

    @Override
    public ObjectDataResponse userAttributes(ObjectDataRequest objectDataRequest) throws Exception {
        return super.userAttributes(objectDataRequest);
    }
}
