package com.manywho.services.box.services;

import com.box.sdk.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.types.*;

import java.util.List;
import java.util.stream.Collectors;

public class ObjectMapperService {

    public ObjectCollection convertBoxComments(List<BoxComment.Info> comments) {
        return comments.stream().map(this::convertBoxComment)
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    public Object convertBoxComment(BoxComment.Info comment) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", comment.getID()));
        properties.add(new Property("Message", comment.getMessage()));

        Object object = new Object();
        object.setDeveloperName(Comment.NAME);
        object.setExternalId(comment.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxFileBasic(BoxFile.Info info) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", info.getID()));
        properties.add(new Property("Name", info.getName()));
        properties.add(new Property("Description", info.getDescription()));
        properties.add(new Property("Created At", info.getCreatedAt()));
        properties.add(new Property("Modified At", info.getModifiedAt()));

        Object object = new Object();
        object.setDeveloperName(File.NAME);
        object.setExternalId(info.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxFile(BoxFile.Info fileInfo, String content) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", fileInfo.getID()));
        properties.add(new Property("Name", fileInfo.getName()));
        properties.add(new Property("Description", fileInfo.getDescription()));
        properties.add(new Property("Content", content));
        properties.add(new Property("Parent Folder", convertBoxFolder(fileInfo.getParent())));
        properties.add(new Property("Comments", convertBoxComments(fileInfo.getResource().getComments())));
        properties.add(new Property("Created At", fileInfo.getCreatedAt()));
        properties.add(new Property("Modified At", fileInfo.getModifiedAt()));

        Object object = new Object();
        object.setDeveloperName(File.NAME);
        object.setExternalId(fileInfo.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxFolder(BoxFolder.Info info) {
        ObjectCollection files = StreamUtils.asStream(info.getResource().iterator())
                .filter(item -> item instanceof BoxFile.Info)
                .map(file -> convertBoxFileBasic((BoxFile.Info) file))
                .collect(Collectors.toCollection(ObjectCollection::new));

        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", info.getID()));
        properties.add(new Property("Name", info.getName()));
        properties.add(new Property("Description", info.getDescription()));
        properties.add(new Property("Files", files));
        properties.add(new Property("Created At", info.getCreatedAt()));
        properties.add(new Property("Modified At", info.getModifiedAt()));

        Object object = new Object();
        object.setDeveloperName(Folder.NAME);
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
        properties.add(new Property("___file", new ObjectCollection(convertBoxFileBasic(file.getInfo(BoxFile.ALL_FIELDS)))));

        Object object = new Object();
        object.setDeveloperName(objectDataType.getDeveloperName());
        object.setExternalId(metadata.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertGroupObjectToManyWhoGroup(BoxGroup.Info group) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("AuthenticationId", group.getID()));
        properties.add(new Property("FriendlyName", group.getName()));
        properties.add(new Property("DeveloperSummary", group.getName()));

        Object object = new Object();
        object.setDeveloperName("GroupAuthorizationGroup");
        object.setExternalId(group.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertUserObjectToManyWhoUser(BoxUser.Info user) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("AuthenticationId", user.getID()));
        properties.add(new Property("FriendlyName", user.getName()));
        properties.add(new Property("DeveloperSummary", user.getName()));

        Object object = new Object();
        object.setDeveloperName("GroupAuthorizationUser");
        object.setExternalId(user.getID());
        object.setProperties(properties);

        return object;
    }
}
