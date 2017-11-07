package com.manywho.services.box.entities.types;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.joda.time.DateTime;

import java.util.List;

@Type(com.manywho.services.box.types.File.NAME)
public class File {
    @Id
    @Property("ID")
    private String id;

    @Property("Name")
    private String name;

    @Property("Description")
    private String description;

    @Property("Content")
    private String content;

    @Property(value = "Parent Folder ID")
    private String parentFolderId;

    @Property(value = "Comments", isList = true)
    private List<Comment> comments;

    @Property(value = "Created At")
    private DateTime createdAt;

    @Property(value = "Modified At")
    private DateTime modifiedAt;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getContent() {
        return content;
    }

    public String getParentFolderId() {
        return parentFolderId;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }
}
