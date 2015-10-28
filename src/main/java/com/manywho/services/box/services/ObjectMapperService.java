package com.manywho.services.box.services;

import com.box.sdk.BoxComment;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxGroup;
import com.box.sdk.BoxTask;
import com.box.sdk.BoxUser;
import com.box.sdk.Metadata;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.services.box.types.Comment;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;
import com.manywho.services.box.types.Task;

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

    public Object convertBoxFile(BoxFile.Info info, boolean loadFull) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", info.getID()));
        properties.add(new Property("Name", info.getName()));
        properties.add(new Property("Description", info.getDescription()));
        properties.add(new Property("Parent Folder", convertBoxFolder(info.getParent())));
        properties.add(new Property("Comments", loadFull ? convertBoxComments(info.getResource().getComments()) : null));
        properties.add(new Property("Created At", info.getCreatedAt()));
        properties.add(new Property("Modified At", info.getModifiedAt()));

        Object object = new Object();
        object.setDeveloperName(File.NAME);
        object.setExternalId(info.getID());
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
        properties.add(new Property("___file", new ObjectCollection(convertBoxFile(file.getInfo(BoxFile.ALL_FIELDS), false))));

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
