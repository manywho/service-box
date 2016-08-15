package com.manywho.services.box.controllers;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxMetadataTemplate;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.facades.BoxFacade;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.CallbackService;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Path("/callback")
public class LaunchFlow {
    AuthenticationService authenticationService;
    BoxFacade boxFacade;
    AbstractOauth2Provider oauth2Provider;
    CallbackService callbackService;

    @Inject
    public LaunchFlow(AuthenticationService authenticationService, BoxFacade boxFacade, AbstractOauth2Provider oauth2Provider, CallbackService callbackService) {
        this.oauth2Provider = oauth2Provider;
        this.authenticationService = authenticationService;
        this.boxFacade = boxFacade;
        this.callbackService = callbackService;
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/flow-execution")
    @GET
    public Response callback(@QueryParam("auth_code") String authCode,
                             @QueryParam("file_id") String fileId,
                             @QueryParam("redirect_to_box") String redirectToBox,
                             @QueryParam("user_id") String user_id) throws Exception {

        BoxAPIConnection apiConnection = authenticationService.authenticateUserWithBox(oauth2Provider.getClientId(), oauth2Provider.getClientSecret(), authCode);
        BoxFile boxFile = new BoxFile(apiConnection, fileId);

        List<ExecutionFlowMetadata> fileMetadata = callbackService.getAllPossibleExecutionFlowMetadata(boxFile);

        if (fileMetadata.size() <1 ) {
            throw new Exception("There is not metadata template for this file");
        }

        BoxDeveloperEditionAPIConnection developerApiConnection = boxFacade.createDeveloperApiConnection(callbackService.getEnterpriseIdFromMetadata(fileMetadata));

        List<BoxMetadataTemplate.Info> accountTemplates = BoxMetadataTemplate.getEnterpriseTemplates(developerApiConnection);
        callbackService.overwriteNullValuesWithDefaultOptions(fileMetadata, accountTemplates);

        List<ExecutionFlowMetadata> executionFlowMetadatas = fileMetadata.stream()
                .filter(ExecutionFlowMetadata::canExecute)
                .collect(Collectors.toList());

        if(executionFlowMetadatas.size() > 1) {
            throw new Exception("More than one flow attached to this file");
        }

        if(executionFlowMetadatas.size() < 1) {
            throw new Exception("There is not flow attached to this file");
        }

        String urlRedirection = String.format(
                "https://flow.manywho.com/67204d5c-6022-474d-8f80-0d576b43d02d/play/default?flow-id=%s&flow-version-id=%s",
                executionFlowMetadatas.get(0).getFlowId(),
                executionFlowMetadatas.get(0).getFlowVersionId());

        return Response.temporaryRedirect(new URI(urlRedirection)).build();
    }
}
