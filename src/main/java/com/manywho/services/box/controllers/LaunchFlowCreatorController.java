package com.manywho.services.box.controllers;

import com.box.sdk.BoxAPIConnection;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.EngineInitializationResponse;
import com.manywho.sdk.services.oauth.AbstractOauth2Provider;
import com.manywho.services.box.configuration.FlowConfiguration;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.managers.CacheManager;
import com.manywho.services.box.managers.LaunchFlowManager;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.FlowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/callback")
public class LaunchFlowCreatorController {
    private LaunchFlowManager launchFlowManager;
    private CacheManager cacheManager;
    private AuthenticationService authenticationService;
    private FlowService flowService;
    private AbstractOauth2Provider oauth2Provider;
    private FlowConfiguration flowConfiguration;
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    public LaunchFlowCreatorController(LaunchFlowManager launchFlowManager, CacheManager cacheManager,
                                       AuthenticationService authenticationService, FlowService flowService,
                                       AbstractOauth2Provider oauth2Provider, FlowConfiguration flowConfiguration) {
        this.launchFlowManager = launchFlowManager;
        this.cacheManager = cacheManager;
        this.authenticationService = authenticationService;
        this.flowService = flowService;
        this.oauth2Provider = oauth2Provider;
        this.flowConfiguration = flowConfiguration;
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/flow-execution")
    @GET
    /**
     * This call is done when a user click in the link provided by the Box Web App Integrations
     *
     * Save information about the file and the metadata for configure a webhook
     * This information will be used when the webhooks calls to initialize and execute the flow described in the metadata.
     *
     * @param authCode
     * @param fileId
     * @param redirectToBox
     * @param user_id
     * @return
     * @throws Exception
     */
    public Response callback(@QueryParam("auth_code") String authCode, @QueryParam("file_id") String fileId,
                             @QueryParam("redirect_to_box") String redirectToBox, @QueryParam("user_id") String userId)
            throws Exception {

        BoxAPIConnection apiConnection = authenticationService.authenticateUserWithBox(oauth2Provider.getClientId(),
                oauth2Provider.getClientSecret(), authCode);

        ExecutionFlowMetadata executionFlowMetadata = this.launchFlowManager.getExecutionFlowMetadata(apiConnection.getAccessToken(), fileId);

        LOGGER.info(executionFlowMetadata);

        if (executionFlowMetadata.getTrigger()!= null) {
            if( cacheManager.getFlowListener("file", fileId, executionFlowMetadata.getTrigger()) == null) {
                FlowId flowId = new FlowId("b7b520d1-f8e1-4a81-b704-28459ec048a4", "f305045c-a320-4d5b-8482-fb1526a240e3");
                String tenantId = "2d925770-d411-42b9-9ea3-7ce669397d82";

                EngineInitializationResponse response = flowService.startFlow(tenantId, flowId,
                        executionFlowMetadata, "file", fileId, null);

                String urlRedirect = String.format("https://flow.manywho.com/%s/play/default?join=%s", tenantId,
                        response.getStateId());

                return Response.temporaryRedirect(new URI(urlRedirect)).build();
            } else {
                throw new Exception("This trigger already exist for this file");
            }
        }

        String urlRedirection = String.format(
                "https://flow.manywho.com/%s/play/default?flow-id=%s&flow-version-id=%s",
                executionFlowMetadata.getTenantId(),
                executionFlowMetadata.getFlowId(),
                executionFlowMetadata.getFlowVersionId());

        return Response.temporaryRedirect(new URI(urlRedirection)).build();
    }
}
