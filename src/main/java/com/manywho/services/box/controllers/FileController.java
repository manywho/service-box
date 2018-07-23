package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractController;
import com.manywho.services.box.managers.FileManager;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/file")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
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
        String selectedFolder = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "0";

        return new ObjectDataResponse(fileManager.loadFiles(getAuthenticatedWho(), selectedFolder));
    }

    @POST
    @Path("/content")
    @Consumes({MediaType.MULTIPART_FORM_DATA, MediaType.APPLICATION_OCTET_STREAM})
    @AuthorizationRequired
    public ObjectDataResponse uploadFile(@FormDataParam("FileDataRequest") FileDataRequest fileDataRequest, FormDataMultiPart file) throws Exception {
        return fileManager.uploadFile(getAuthenticatedWho(), fileDataRequest, file);
    }
}
