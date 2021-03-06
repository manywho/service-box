package com.manywho.services.box.services;

import com.box.sdk.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.services.box.types.*;

import java.util.Objects;
import java.util.stream.Collectors;

public class ObjectMapperService {

    public Object convertBoxComment(BoxComment.Info comment, BoxFile.Info fileInfo) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", comment.getID()));
        properties.add(new Property("Message", comment.getMessage()));
        properties.add(new Property("File", convertBoxFile(fileInfo, null)));

        Object object = new Object();
        object.setDeveloperName(Comment.NAME);
        object.setExternalId(comment.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxFile(BoxFile.Info fileInfo, String content) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", fileInfo.getID()));
        properties.add(new Property("Name", fileInfo.getName()));
        properties.add(new Property("Description", fileInfo.getDescription()));
        properties.add(new Property("Content", content));
        if (fileInfo.getParent() != null) {
            properties.add(new Property("Parent Folder", convertBoxFolder(fileInfo.getParent())));
        } else {
            properties.add(new Property("Parent Folder", new ObjectCollection()));
        }

        properties.add(new Property("Created At", fileInfo.getCreatedAt()));
        properties.add(new Property("Modified At", fileInfo.getModifiedAt()));

        Object object = new Object();
        object.setDeveloperName(File.NAME);
        object.setExternalId(fileInfo.getID());
        object.setProperties(properties);

        return object;
    }

    /**
     * The properties of this object will match with the $File object
     */
    public Object convertToManyWhoFile(BoxFile.Info fileInfo, String content) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("Date Created", fileInfo.getCreatedAt()));
        properties.add(new Property("Date Modified", fileInfo.getModifiedAt()));
        properties.add(new Property("Description", fileInfo.getDescription()));
        properties.add(new Property("Download Uri", ""));
        properties.add(new Property("Embed Uri", ""));
        properties.add(new Property("Icon Uri", ""));
        properties.add(new Property("Id", fileInfo.getID()));
        properties.add(new Property("Name", fileInfo.getName()));
        properties.add(new Property("Kind", fileInfo.getExtension()));
        properties.add(new Property("Mime Type", ""));

        Object object = new Object();
        object.setDeveloperName(File.NAME);
        object.setExternalId(fileInfo.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxFolder(BoxFolder.Info info) {
        return convertBoxFolderInternal(info, false);
    }

    public Object convertBoxFolderInternal(BoxFolder.Info info, Boolean emptyParentFolder) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", info.getID()));
        properties.add(new Property("Name", info.getName()));
        properties.add(new Property("Description", info.getDescription()));
        properties.add(new Property("Created At", info.getCreatedAt()));
        properties.add(new Property("Modified At", info.getModifiedAt()));

        if (emptyParentFolder || Objects.equals(info.getID(), "0") || info.getParent() == null ) {
            properties.add(new Property("Parent Folder", new ObjectCollection()));
        } else {
            properties.add(new Property("Parent Folder", convertBoxFolderInternal(info.getParent(), true)));
        }

        Object object = new Object();
        object.setDeveloperName(Folder.NAME);
        object.setExternalId(info.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertBoxTask(BoxTask.Info taskInfo, BoxFile.Info fileInfo) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", taskInfo.getID()));
        properties.add(new Property("Due At", taskInfo.getDueAt()));
        properties.add(new Property("Message", taskInfo.getMessage()));
        properties.add(new Property("Is Completed?", taskInfo.isCompleted()));
        properties.add(new Property("Created At", taskInfo.getCreatedAt()));
        properties.add(new Property("File", convertBoxFile(fileInfo, null)));

        Object object = new Object();
        object.setDeveloperName(Task.NAME);
        object.setExternalId(taskInfo.getID());
        object.setProperties(properties);

        return object;
    }


    public Object convertBoxTaskAssignment(BoxTaskAssignment.Info taskAssignmentInfo, BoxFile.Info fileInfo) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", taskAssignmentInfo.getID()));
        properties.add(new Property("Assignee Email", taskAssignmentInfo.getAssignedTo().getLogin()));
        properties.add(new Property("File", convertBoxFile(fileInfo, null)));

        Object object = new Object();
        object.setDeveloperName(TaskAssignment.NAME);
        object.setExternalId(taskAssignmentInfo.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertFileMetadata(BoxFile file, ObjectDataType objectDataType) {
        Metadata metadata = file.getMetadata( objectDataType.getDeveloperName());

        // Populate all the desired properties from the values in the metadata (except for the virtual ___file field)
        PropertyCollection properties = objectDataType.getProperties()
                .stream()
                .filter(property -> !property.getDeveloperName().equals("___file"))
                .map(property -> new Property(property.getDeveloperName(), metadata.get("/" + property.getDeveloperName())))
                .collect(Collectors.toCollection(PropertyCollection::new));

        // Add the virtual ___file field
        properties.add(new Property("___file", new ObjectCollection(convertBoxFile(file.getInfo(BoxFile.ALL_FIELDS), null))));

        Object object = new Object();
        object.setDeveloperName(objectDataType.getDeveloperName());
        object.setExternalId(metadata.getID());
        object.setProperties(properties);

        return object;
    }

    public Object convertFolderMetadata(BoxFolder folder, ObjectDataType objectDataType) {
        Metadata metadata = folder.getMetadata(objectDataType.getDeveloperName());

        // Populate all the desired properties from the values in the metadata (except for the virtual ___file field)
        PropertyCollection properties = objectDataType.getProperties()
                .stream()
                .filter(property -> !property.getDeveloperName().equals("___folder"))
                .map(property -> new Property(property.getDeveloperName(), metadata.get("/" + property.getDeveloperName())))
                .collect(Collectors.toCollection(PropertyCollection::new));

        // Add the virtual ___file field
        properties.add(new Property("___folder", new ObjectCollection(convertBoxFolder(folder.getInfo(BoxFolder.ALL_FIELDS)))));

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

    public MObject convertWebhook(BoxWebHook.Info webhook) {
        PropertyCollection properties = new PropertyCollection();
        properties.add(new Property("ID", webhook.getID()));
        properties.add(new Property("Target Type", webhook.getTarget().getType()));
        properties.add(new Property("Target ID", webhook.getTarget().getId()));

        Object object = new Object();
        object.setDeveloperName(Webhook.NAME);
        object.setExternalId(webhook.getID());
        object.setProperties(properties);

        return object;
    }
}
