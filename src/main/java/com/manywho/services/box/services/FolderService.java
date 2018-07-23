package com.manywho.services.box.services;

import com.box.sdk.BoxFolder;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.services.box.client.BoxClient;

import javax.inject.Inject;

public class FolderService {
    private BoxClient boxClient;
    private ObjectMapperService objectMapperService;

    @Inject
    public FolderService(BoxClient boxClient, ObjectMapperService objectMapperService) {
        this.boxClient = boxClient;
        this.objectMapperService = objectMapperService;
    }

    public Object createFolder(String token, String parentFolderId, String name) throws Exception {
        BoxFolder.Info folder = boxClient.createFolder(token, parentFolderId, name);
        if (folder == null) {
            throw new Exception("Unable to create a folder with the name " + name);
        }

        return objectMapperService.convertBoxFolder(folder);
    }
}
