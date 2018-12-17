package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.services.EncryptService;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/token")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TokenVerificationController extends AbstractController {
    @Inject
    EncryptService encryptService;

    @Path("/verification")
    @POST
    @AuthorizationRequired
    public ServiceResponse tokenVerification(ServiceRequest serviceRequest) throws Exception {
        AuthenticatedWho authenticatedWho = getAuthenticatedWho();
        String encryptedData = encryptService.encryptData(authenticatedWho.getToken());

        return new ServiceResponse(InvokeType.Forward,
                new EngineValue("Verification Token", ContentType.String, encryptedData),
                serviceRequest.getToken());
    }
}
