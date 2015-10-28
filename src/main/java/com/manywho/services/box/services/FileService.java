package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxSharedLink;
import com.box.sdk.BoxSharedLink.Access;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.services.box.facades.BoxFacade;

import javax.inject.Inject;

public class FileService {
    @Inject
    private BoxFacade boxFacade;

    public Object buildManyWhoFileObject(BoxFile.Info fileInformation, BoxFile file) {
        BoxSharedLink.Permissions permissions = new BoxSharedLink.Permissions();
        permissions.setCanDownload(true);

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Kind", fileInformation.getExtension()));
        properties.add(new Property("ID", fileInformation.getID()));
        properties.add(new Property("Mime Type"));
        properties.add(new Property("Name", fileInformation.getName()));
        properties.add(new Property("Description", fileInformation.getDescription()));
        properties.add(new Property("Date Created", fileInformation.getCreatedAt()));
        properties.add(new Property("Date Modified", fileInformation.getModifiedAt()));
        properties.add(new Property("Download Uri", file.createSharedLink(Access.DEFAULT, null, permissions).getURL()));
        properties.add(new Property("Embed Uri"));
        properties.add(new Property("Icon Uri"));

        Object object = new Object();
        object.setDeveloperName("$File");
        object.setExternalId(fileInformation.getID());
        object.setProperties(properties);

        return object;
    }

    public void copyFile(String token, String fileId, String folderId, String newName) {
        boxFacade.copyFile(token, fileId, folderId, newName);
    }

    public void moveFile(String token, String fileId, String folderId, String newName) {
        boxFacade.moveFile(token, fileId, folderId, newName);
    }
}
