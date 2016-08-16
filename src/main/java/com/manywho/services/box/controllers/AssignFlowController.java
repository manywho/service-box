package com.manywho.services.box.controllers;

import com.box.sdk.BoxAPIConnection;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.entities.ExecutionFlowMetadata;
import com.manywho.services.box.entities.requests.AssignFlowWebhookCreate;
import com.manywho.services.box.managers.LaunchFlowManager;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/assign-webhook")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssignFlowController extends AbstractController{
    private LaunchFlowManager launchFlowManager;
    private PropertyCollectionParser propertyParser;

    @Inject
    public AssignFlowController(LaunchFlowManager launchFlowManager, PropertyCollectionParser propertyParser) {
        this.launchFlowManager = launchFlowManager;
        this.propertyParser = propertyParser;
    }

    @Path("/flow")
    @POST
    @AuthorizationRequired
    public ServiceResponse assignFlowToWebhook(ServiceRequest serviceRequest) throws Exception {
        AssignFlowWebhookCreate assignFlow = propertyParser.parse(serviceRequest.getInputs(), AssignFlowWebhookCreate.class);
        ExecutionFlowMetadata executionFlowMetadata = new ExecutionFlowMetadata();
        executionFlowMetadata.setTrigger(assignFlow.getTrigger());
        executionFlowMetadata.setFlowId(assignFlow.getFlowId());
        executionFlowMetadata.setFlowVersionId(assignFlow.getFlowVersionId());
        executionFlowMetadata.setTenantId(assignFlow.getTenantId());

        BoxAPIConnection boxAPIConnection = new BoxAPIConnection(getAuthenticatedWho().getToken());

        launchFlowManager.createFlowListener(assignFlow.getTargetId(), boxAPIConnection , executionFlowMetadata);

        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }
}
