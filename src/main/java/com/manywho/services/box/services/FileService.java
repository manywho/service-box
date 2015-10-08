package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.services.box.facades.BoxFacade;

import javax.inject.Inject;

public class FileService {
    @Inject
    private BoxFacade boxFacade;

    public Object buildManyWhoFileObject(BoxFile.Info fileInformation) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Kind", fileInformation.getExtension()));
        properties.add(new Property("ID", fileInformation.getID()));
        properties.add(new Property("Mime Type"));
        properties.add(new Property("Name", fileInformation.getName()));
        properties.add(new Property("Description", fileInformation.getDescription()));
        properties.add(new Property("Date Created", fileInformation.getCreatedAt()));
        properties.add(new Property("Date Modified", fileInformation.getModifiedAt()));
        properties.add(new Property("Download Uri", ""));
        properties.add(new Property("Embed Uri"));
        properties.add(new Property("Icon Uri"));

        Object object = new Object();
        object.setDeveloperName("$File");
        object.setExternalId(fileInformation.getID());
        object.setProperties(properties);

        return object;
    }
}
