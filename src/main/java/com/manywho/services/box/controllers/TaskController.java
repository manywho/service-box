package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.managers.TaskManager;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/task")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskController extends AbstractController {

    @Inject
    private TaskManager taskManager;

    @Path("/create")
    @POST
    @AuthorizationRequired
    public ServiceResponse createTask(ServiceRequest serviceRequest) throws Exception {
        return taskManager.createTask(getAuthenticatedWho(), serviceRequest);
    }

    @Path("/addassignment")
    @POST
    @AuthorizationRequired
    public ServiceResponse addAssignment(ServiceRequest serviceRequest) throws Exception {
        return taskManager.addAssignment(getAuthenticatedWho(), serviceRequest);
    }
}
