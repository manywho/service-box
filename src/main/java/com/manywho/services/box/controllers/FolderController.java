package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.managers.FolderManager;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/folder")
@Consumes("application/json")
@Produces("application/json")
public class FolderController extends AbstractController {

    @Inject
    private FolderManager folderManager;

    @Path("/create")
    @POST
    @AuthorizationRequired
    public ServiceResponse createFolder(ServiceRequest serviceRequest) throws Exception {
        return folderManager.createFolder(getAuthenticatedWho(), serviceRequest);
    }
}
