package com.manywho.services.box.entities;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;
import org.joda.time.DateTime;

@Type(com.manywho.services.box.types.Task.NAME)
public class Task {
    @Id
    @Property("ID")
    private String id;

    @Property("Due At")
    private DateTime dueAt;

    @Property("Message")
    private String message;

    @Property("Is Completed?")
    private boolean completed;

    @Property("Created At")
    private DateTime createdAt;

    public String getId() {
        return id;
    }

    public DateTime getDueAt() {
        return dueAt;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCompleted() {
        return completed;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }
}
