package com.manywho.services.box.controllers;

import com.box.sdk.BoxAPIConnection;
import com.manywho.sdk.entities.draw.flow.FlowId;
import com.manywho.sdk.entities.run.EngineInitializationResponse;
import com.manywho.services.box.configuration.FlowConfiguration;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.entities.webhook.Item;
import com.manywho.services.box.managers.CacheManagerInterface;
import com.manywho.services.box.managers.LaunchFlowManager;
import com.manywho.services.box.services.AuthenticationService;
import com.manywho.services.box.services.FlowService;
import com.manywho.services.box.utilities.ParseUrlUtility;
import org.apache.commons.lang3.StringUtils;
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
    private CacheManagerInterface cacheManager;
    private AuthenticationService authenticationService;
    private FlowService flowService;
    private FlowConfiguration flowConfiguration;

    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    @Inject
    public LaunchFlowCreatorController(LaunchFlowManager launchFlowManager, CacheManagerInterface cacheManager,
                                       AuthenticationService authenticationService, FlowService flowService,
                                       FlowConfiguration flowConfiguration) {
        this.launchFlowManager = launchFlowManager;
        this.cacheManager = cacheManager;
        this.authenticationService = authenticationService;
        this.flowService = flowService;
        this.flowConfiguration = flowConfiguration;
    }

    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/flow-execution")
    @GET
    /**
     * This call is done when a user click in the link provided by the Box Web App Integrations
     * we save in cache the item id and type, the item can be fetched afterwards using an action
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
                             @QueryParam("redirect_to_box") String redirectToBox, @QueryParam("user_id") String userId,
                             @QueryParam("flow_uri") String flowUri, @QueryParam("trigger") String trigger)
            throws Exception {
        try {
            BoxAPIConnection apiConnection = authenticationService.authenticateUserWithBox(authCode);

            ExecutionFlowMetadata executionFlowMetadata;

            if (!StringUtils.isEmpty(flowUri) && !StringUtils.isEmpty(trigger)) {
                executionFlowMetadata = new ExecutionFlowMetadata();
                executionFlowMetadata.setFlowId(ParseUrlUtility.getFlowId(flowUri));
                executionFlowMetadata.setFlowVersionId(ParseUrlUtility.getFlowVersionId(flowUri));
                executionFlowMetadata.setTenantId(ParseUrlUtility.getTenantId(flowUri));
                executionFlowMetadata.setTrigger(trigger);

            } else {
                executionFlowMetadata = this.launchFlowManager.getExecutionFlowMetadata(apiConnection.getAccessToken(), fileId);
            }

            if (executionFlowMetadata.getTrigger() != null) {
                if (cacheManager.getFlowListener("file", fileId, executionFlowMetadata.getTrigger()) == null) {
                    String urlRedirect = initializeFlow(fileId, executionFlowMetadata);

                    return Response.temporaryRedirect(new URI(urlRedirect)).build();
                } else {
                    throw new Exception("This trigger already exist for this file");
                }
            }
            EngineInitializationResponse response = flowService.initializeFlowWithoutAuthentication(executionFlowMetadata);

            cacheManager.saveIntegrationItem(response.getStateId(), new Item(fileId, "file"));

            String urlRedirection = String.format(
                    "https://flow.manywho.com/%s/play/default?join=%s",
                    executionFlowMetadata.getTenantId(),
                    response.getStateId());

            return Response.temporaryRedirect(new URI(urlRedirection)).build();
        }catch (Exception ex) {
            LOGGER.debug(ex.getMessage());

            throw ex;
        }
    }

    private String initializeFlow(@QueryParam("file_id") String fileId, ExecutionFlowMetadata executionFlowMetadata) throws Exception {

        FlowId flowId;
        if (flowConfiguration.getAssignmentFlowVersionId() != null) {
            flowId = new FlowId(flowConfiguration.getAssignmentFlowId(), flowConfiguration.getAssignmentFlowVersionId());
        } else {
            flowId = new FlowId(flowConfiguration.getAssignmentFlowId());
        }

        EngineInitializationResponse response = flowService.startFlow(flowConfiguration.getAssignmentTenantId(), flowId,
                executionFlowMetadata, "file", fileId, null);

        return String.format("https://flow.manywho.com/%s/play/default?join=%s",
                flowConfiguration.getAssignmentTenantId(), response.getStateId());
    }
}
