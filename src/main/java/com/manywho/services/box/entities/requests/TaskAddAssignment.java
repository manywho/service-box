package com.manywho.services.box.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.Task;

public class TaskAddAssignment {
    @Property(value = "Task", isObject = true)
    private Task task;

    @Property("Assignee Email")
    private String assigneeEmail;

    public Task getTask() {
        return task;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }
}
