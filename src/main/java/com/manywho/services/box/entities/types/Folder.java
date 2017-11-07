package com.manywho.services.box.entities.types;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

@Type(com.manywho.services.box.types.Folder.NAME)
public class Folder {

    @Id
    @Property("ID")
    private String id;

    @Property("Name")
    private String name;

    @Property("Description")
    private String description;

    @Property(value = "Parent Folder ID")
    private String parentFolderId;

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

    public String getParentFolderId() {
        return parentFolderId;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public DateTime getModifiedAt() {
        return modifiedAt;
    }
}
