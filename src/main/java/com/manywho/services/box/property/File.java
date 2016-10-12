package com.manywho.services.box.property;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import java.util.List;

@Type(com.manywho.services.box.types.File.NAME)
public class File {
    @Id
    @Property("ID")
    private String id;

    @Property("Name")
    @NotEmpty(message = "Folder Name cannot be null or empty")
    private String name;

    @Property("Description")
    private String description;

    @Property("Content")
    private String content;

    @Property(value = "Parent Folder", isObject = true)
    @NotNull(message = "The Parent Folder can not be null or empty")
    private Folder parentFolder;

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

    public Folder getParentFolder() {
        return parentFolder;
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
