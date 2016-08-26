package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.entities.requests.AssignFlowWebhookCreate;
import com.manywho.services.box.managers.AssignFlowManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/trigger-assign")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AssignFlowController extends AbstractController{
    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());
    private PropertyCollectionParser propertyParser;
    private AssignFlowManager assignFlowManager;

    @Inject
    public AssignFlowController(PropertyCollectionParser propertyParser,
                                AssignFlowManager assignFlowManager) {
        this.propertyParser = propertyParser;
        this.assignFlowManager = assignFlowManager;
    }

    @Path("/flow")
    @POST
    @AuthorizationRequired
    public ServiceResponse assignFlowToWebhook(ServiceRequest serviceRequest) throws Exception {
        AssignFlowWebhookCreate assignFlow = propertyParser.parse(serviceRequest.getInputs(), AssignFlowWebhookCreate.class);
        LOGGER.info(assignFlow);

        assignFlowManager.assignFlowToWebhook(assignFlow, getAuthenticatedWho(),
                request.getHeaders().get("authorization").get(0));

        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }
}
