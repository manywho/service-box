package com.manywho.services.box.controllers;

import com.box.sdk.BoxAPIConnection;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.EngineInitializationResponse;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.managers.CacheManager;
import com.manywho.services.box.managers.LaunchFlowManager;
import com.manywho.services.box.managers.WebhookManager;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.FlowService;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/callback")
public class CallbackLaunchFlowController {
    LaunchFlowManager launchFlowManager;
    CacheManager cacheManager;
    WebhookManager webhookService;
    AuthenticationService authenticationService;
    FlowService flowService;
    AbstractOauth2Provider oauth2Provider;


    @Inject
    public CallbackLaunchFlowController(LaunchFlowManager launchFlowManager, CacheManager cacheManager,
                                        WebhookManager webhookService, AuthenticationService authenticationService,
                                        FlowService flowService, AbstractOauth2Provider oauth2Provider) {
        this.launchFlowManager = launchFlowManager;
        this.cacheManager = cacheManager;
        this.webhookService = webhookService;
        this.authenticationService = authenticationService;
        this.flowService = flowService;
        this.oauth2Provider = oauth2Provider;
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/flow-execution")
    @GET
    public Response callback(@QueryParam("auth_code") String authCode, @QueryParam("file_id") String fileId,
                             @QueryParam("redirect_to_box") String redirectToBox,
                             @QueryParam("user_id") String user_id) throws Exception {

        BoxAPIConnection apiConnection;
        apiConnection = authenticationService.authenticateUserWithBox(oauth2Provider.getClientId(), oauth2Provider.getClientSecret(), authCode);
        //apiConnection = authenticationService.authenticateUserWithBox("962elg4c0kt15i3j5v8k7bcba1x9hc22", "VauUqXcXvZEPzwCTuu3Pi8TrLD5hZut7", authCode);

        ExecutionFlowMetadata executionFlowMetadata = this.launchFlowManager.getExecutionFlowMetadata(apiConnection.getAccessToken(), fileId);

        if (executionFlowMetadata.getTrigger()!= null &&
                cacheManager.getFlowListener("file", fileId, executionFlowMetadata.getTrigger()) == null) {

            EngineInitializationResponse response = flowService.startFlow(executionFlowMetadata.getTenantId(),
                    new FlowId("b7b520d1-f8e1-4a81-b704-28459ec048a4"), executionFlowMetadata, "file", fileId);

            String urlRedirect = "https://flow.manywho.com/" + executionFlowMetadata.getTenantId() +
                    "/play/default?join=" + response.getStateId();

            return Response.temporaryRedirect(new URI(urlRedirect)).build();
        }

        String urlRedirection = String.format(
                "https://flow.manywho.com/%s/play/default?flow-id=%s&flow-version-id=%s",
                executionFlowMetadata.getTenantId(),
                executionFlowMetadata.getFlowId(),
                executionFlowMetadata.getFlowVersionId());

        return Response.temporaryRedirect(new URI(urlRedirection)).build();
    }
}
