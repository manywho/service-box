package com.manywho.services.box.controllers;

import com.manywho.sdk.entities.run.elements.type.ListFilter;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataRequest;
import com.manywho.sdk.entities.run.elements.type.ObjectDataResponse;
import com.manywho.sdk.services.annotations.AuthorizationRequired;
import com.manywho.sdk.services.controllers.AbstractDataController;
import com.manywho.services.box.managers.DataManager;
import com.manywho.services.box.managers.FolderManager;
import com.manywho.services.box.managers.TaskManager;
import com.manywho.services.box.managers.WebhookManager;
import com.manywho.services.box.types.*;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DataController extends AbstractDataController {

    @Inject
    private DataManager dataManager;

    @Inject
    private FolderManager folderManager;

    @Inject
    private TaskManager taskManager;

    @Inject
    private WebhookManager webhookManager;

    @Override
    public ObjectDataResponse delete(ObjectDataRequest objectDataRequest) throws Exception {
        String dataTypeToDelete = objectDataRequest.getObjectDataType().getDeveloperName();
        if (Webhook.NAME.equals(dataTypeToDelete) && objectDataRequest.getObjectData().size() == 1) {
            webhookManager.deleteWebhookMetadata(getAuthenticatedWho().getToken(), objectDataRequest.getObjectData().get(0).getExternalId());
            webhookManager.deleteWebhook(getAuthenticatedWho().getToken(), objectDataRequest.getObjectData().get(0).getExternalId());

            return new ObjectDataResponse();
        }

        throw new Exception(String.format("Deleting %s isn't currently supported in the Box Service", dataTypeToDelete));
    }

    @Path("/data")
    @POST
    @AuthorizationRequired
    public ObjectDataResponse load(ObjectDataRequest objectDataRequest) throws Exception {
        switch (objectDataRequest.getObjectDataType().getDeveloperName()) {
            case File.NAME:
                return generateResponse(dataManager.loadFileType(getAuthenticatedWho(), objectDataRequest), objectDataRequest);
            case Folder.NAME:
                return generateResponse(dataManager.loadFolderType(getAuthenticatedWho(), objectDataRequest), objectDataRequest);
            case Task.NAME:
                return new ObjectDataResponse(dataManager.loadTask(getAuthenticatedWho(), objectDataRequest));
            case TaskAssignment.NAME:
                return new ObjectDataResponse(dataManager.loadTaskAssignment(getAuthenticatedWho(), objectDataRequest));
            case "$File":
                return new ObjectDataResponse(dataManager.loadFileSystem(getAuthenticatedWho(), objectDataRequest));
            case Comment.NAME:
                return new ObjectDataResponse(dataManager.loadComments(getAuthenticatedWho(), objectDataRequest));
            case Webhook.NAME:
                return new ObjectDataResponse(dataManager.loadWebhooks(getAuthenticatedWho(), objectDataRequest));
            default:
                return generateResponse(dataManager.loadMetadataType(getAuthenticatedWho(), objectDataRequest), objectDataRequest);
        }
    }

    private ObjectDataResponse generateResponse(ObjectCollection objects, ObjectDataRequest objectDataRequest) {
        boolean havMoreElements =  haveMoreElements(objects, objectDataRequest.getListFilter());

        if (havMoreElements) {
            objects = removeElementsAfterLimit(objects, objectDataRequest.getListFilter());
        }

        return new ObjectDataResponse(objects, havMoreElements);
    }

    private boolean haveMoreElements(ObjectCollection collection, ListFilter filter) {
        return filter != null && filter.getLimit() > 0 && collection.size() >= filter.getLimit();
    }

    private ObjectCollection removeElementsAfterLimit(ObjectCollection objectCollection, ListFilter filter) {
        ObjectCollection collection = new ObjectCollection();
        collection.addAll(objectCollection.subList(0, filter.getLimit()));

        return  collection;
    }

    @Path("/data")
    @PUT
    @AuthorizationRequired
    public ObjectDataResponse save(ObjectDataRequest objectDataRequest) throws Exception {
        switch (objectDataRequest.getObjectDataType().getDeveloperName()) {
            case Folder.NAME:
                return new ObjectDataResponse(folderManager.createFolder(getAuthenticatedWho(), objectDataRequest));
            case Task.NAME:
                return new ObjectDataResponse(taskManager.createTask(getAuthenticatedWho(), objectDataRequest));
            case TaskAssignment.NAME:
                throw new RuntimeException("Saving the type Task Assignment isn't supported");
            case File.NAME:
                throw new RuntimeException("Saving the type File isn't supported");
            case "$File":
                throw new RuntimeException("Saving the type $File isn't supported");
            case Comment.NAME:
                throw new RuntimeException("Saving the type Comment isn't supported");
            default:
                return new ObjectDataResponse(dataManager.saveMetadataType(getAuthenticatedWho(), objectDataRequest));
        }
    }
}
