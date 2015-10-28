package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.managers.FileManager;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/file")
@Consumes("application/json")
@Produces("application/json")
public class FileController extends AbstractController {

    @Inject
    private FileManager fileManager;

    @Path("/copy")
    @POST
    @AuthorizationRequired
    public ServiceResponse copyFile(ServiceRequest serviceRequest) throws Exception {
        return fileManager.copyFile(getAuthenticatedWho(), serviceRequest);
    }

    @Path("/move")
    @POST
    @AuthorizationRequired
    public ServiceResponse moveFile(ServiceRequest serviceRequest) throws Exception {
        return fileManager.moveFile(getAuthenticatedWho(), serviceRequest);
    }

    @Path("/")
    @POST
    @AuthorizationRequired
    public ObjectDataResponse loadFiles(FileDataRequest fileDataRequest) throws Exception {
        return fileManager.loadFiles(getAuthenticatedWho(), fileDataRequest);
    }

    @POST
    @Path("/content")
    @Consumes({"multipart/form-data", "application/octet-stream"})
    @AuthorizationRequired
    public ObjectDataResponse uploadFile(@FormDataParam("FileDataRequest") FileDataRequest fileDataRequest, FormDataMultiPart file) throws Exception {
        return fileManager.uploadFile(getAuthenticatedWho(), fileDataRequest, file);
    }
}
