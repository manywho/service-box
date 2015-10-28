package com.manywho.services.box.managers;

import com.manywho.sdk.entities.run.EngineValue;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.box.entities.requests.FolderCreate;
import com.manywho.services.box.services.FolderService;
import com.manywho.services.box.types.Folder;

import javax.inject.Inject;

public class FolderManager {
    @Inject
    private FolderService folderService;

    @Inject
    private PropertyCollectionParser propertyParser;

    public ServiceResponse createFolder(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        FolderCreate folderCreate = propertyParser.parse(serviceRequest.getInputs(), FolderCreate.class);
        if (folderCreate == null) {
            throw new Exception("Unable to parse the incoming FolderCreate request");
        }

        Object folder = folderService.createFolder(user.getToken(), folderCreate.getFolder().getId(), folderCreate.getName());

        EngineValue folderValue = new EngineValue("Folder", ContentType.Object, Folder.NAME, folder);

        return new ServiceResponse(InvokeType.Forward, folderValue, serviceRequest.getToken());
    }
}
