package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractDataController;
import com.manywho.services.box.managers.DataManager;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;
import com.manywho.services.box.types.Task;
import com.manywho.services.box.types.TaskAssignment;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataController extends AbstractDataController {

    @Inject
    private DataManager dataManager;

    @Override
    public ObjectDataResponse delete(ObjectDataRequest objectDataRequest) throws Exception {
        throw new Exception("Deleting isn't currently supported in the Box Service");
    }

    @Path("/data")
    @POST
    @AuthorizationRequired
    public ObjectDataResponse load(ObjectDataRequest objectDataRequest) throws Exception {
        switch (objectDataRequest.getObjectDataType().getDeveloperName()) {
            case File.NAME:
                return new ObjectDataResponse(dataManager.loadFileType(getAuthenticatedWho(), objectDataRequest));
            case Folder.NAME:
                return new ObjectDataResponse(dataManager.loadFolderType(getAuthenticatedWho(), objectDataRequest));
            case Task.NAME:
                return new ObjectDataResponse(dataManager.loadTask(getAuthenticatedWho(), objectDataRequest));
            case TaskAssignment.NAME:
                return new ObjectDataResponse(dataManager.loadTaskAssignment(getAuthenticatedWho(), objectDataRequest));
            case "$File":
                return new ObjectDataResponse(dataManager.loadFileSystem(getAuthenticatedWho(), objectDataRequest));
            default:
                // Assume the type represents Metadata
                return new ObjectDataResponse(dataManager.loadMetadataType(getAuthenticatedWho(), objectDataRequest));
        }
    }

    @Path("/data")
    @PUT
    @AuthorizationRequired
    public ObjectDataResponse save(ObjectDataRequest objectDataRequest) throws Exception {
        switch (objectDataRequest.getObjectDataType().getDeveloperName()) {
            default:
                // Assume the type represents Metadata
                return new ObjectDataResponse(dataManager.saveMetadataType(getAuthenticatedWho(), objectDataRequest));
        }
    }
}