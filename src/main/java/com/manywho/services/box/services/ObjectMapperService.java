package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
//import com.box.sdk.BoxTask;
import com.box.sdk.BoxTask;
import com.box.sdk.Metadata;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Task;

import java.util.stream.Collectors;

public class ObjectMapperService {

    public Object convertBoxFile(BoxFile.Info info) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", info.getID()));
        properties.add(new Property("Name", info.getName()));

        Object object = new Object();
        object.setDeveloperName(File.NAME);
        object.setExternalId(info.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxTask(BoxTask.Info info) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", info.getID()));
        properties.add(new Property("Due At", info.getDueAt()));
        properties.add(new Property("Message", info.getMessage()));
        properties.add(new Property("Is Completed?", info.isCompleted()));
        properties.add(new Property("Created At", info.getCreatedAt()));

        Object object = new Object();
        object.setDeveloperName(Task.NAME);
        object.setExternalId(info.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertFileMetadata(BoxFile file, ObjectDataType objectDataType) {
        Metadata metadata = file.getMetadata("enterprise/" + objectDataType.getDeveloperName());

        // Populate all the desired properties from the values in the metadata (except for the virtual ___file field)
        PropertyCollection properties = objectDataType.getProperties()
                .stream()
                .filter(property -> !property.getDeveloperName().equals("___file"))
                .map(property -> new Property(property.getDeveloperName(), metadata.get("/" + property.getDeveloperName())))
                .collect(Collectors.toCollection(PropertyCollection::new));

        // Add the virtual ___file field
        properties.add(new Property("___file", new ObjectCollection(convertBoxFile(file.getInfo()))));

        Object object = new Object();
        object.setDeveloperName(objectDataType.getDeveloperName());
        object.setExternalId(metadata.getID());
        object.setProperties(properties);

        return object;
    }
}
