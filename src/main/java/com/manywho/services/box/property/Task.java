package com.manywho.services.box.property;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

@Type(com.manywho.services.box.types.Task.NAME)
public class Task {
    @Id
    @Property("ID")
    private String id;

    @Property(value = "Modified At")
    private DateTime dueAt;

    @Property(value = "Message")
    private String message;

    @Property(value = "Is Completed?")
    private Boolean completed;

    @Property(value = "Created At")
    private DateTime createdAt;

    @Property(value = "File", isObject = true)
    @NotNull(message = "The File can not be empty")
    private File file;

    public String getId() {
        return id;
    }

    public DateTime getDueAt() {
        return dueAt;
    }

    public String getMessage() {
        return message;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public File getFile() {
        return file;
    }
}
