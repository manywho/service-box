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
                ObjectCollection collection = dataManager.loadFileType(getAuthenticatedWho(), objectDataRequest);
                boolean hasMore = removeOneElementsAfterIndex(collection, objectDataRequest.getListFilter());

                return new ObjectDataResponse(collection, hasMore);

            case Folder.NAME:
                ObjectCollection collectionOfFolders = dataManager.loadFolderType(getAuthenticatedWho(), objectDataRequest);
                boolean hasMoreFolders = removeOneElementsAfterIndex(collectionOfFolders, objectDataRequest.getListFilter());

                return new ObjectDataResponse(collectionOfFolders, hasMoreFolders);
            case Task.NAME:
                return new ObjectDataResponse(dataManager.loadTask(getAuthenticatedWho(), objectDataRequest));
            case TaskAssignment.NAME:
                return new ObjectDataResponse(dataManager.loadTaskAssignment(getAuthenticatedWho(), objectDataRequest));
            case "$File":
                return new ObjectDataResponse(dataManager.loadFileSystem(getAuthenticatedWho(), objectDataRequest));
            case Comment.NAME:
                return new ObjectDataResponse(dataManager.loadComments(getAuthenticatedWho(), objectDataRequest));
            default:
                // Assume the type represents Metadata
                return new ObjectDataResponse(dataManager.loadMetadataType(getAuthenticatedWho(), objectDataRequest));
        }
    }

    private boolean removeOneElementsAfterIndex(ObjectCollection objectCollection, ListFilter filter) {
        if (filter != null && filter.getLimit() > 0 && objectCollection.size() > filter.getLimit()) {
            objectCollection.remove(filter.getLimit());
            return true;
        } else{
            return false;
        }
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