package com.manywho.services.box.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.File;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

public class TaskCreate {
    @Property(value = "File", isObject = true)
    @NotNull(message = "A file is required when creating a task")
    private File file;

    @Property("Message")
    private String message;

    @Property("Due At")
    private DateTime dueAt;

    public File getFile() {
        return file;
    }

    public String getMessage() {
        return message;
    }

    public DateTime getDueAt() {
        return dueAt;
    }
}
