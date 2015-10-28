package com.manywho.services.box.services;

import com.box.sdk.BoxFolder;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.services.box.facades.BoxFacade;

import javax.inject.Inject;

public class FolderService {
    @Inject
    private BoxFacade boxFacade;

    @Inject
    private ObjectMapperService objectMapperService;

    public Object createFolder(String token, String parentFolderId, String name) throws Exception {
        BoxFolder.Info folder = boxFacade.createFolder(token, parentFolderId, name);
        if (folder == null) {
            throw new Exception("Unable to create a folder with the name " + name);
        }

        return objectMapperService.convertBoxFolder(folder);
    }
}
