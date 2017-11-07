package com.manywho.services.box.entities.actions;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.types.Task;

public class TaskAddAssignment {
    @Property(value = "Task ID")
    private String taskId;

    @Property("Assignee Email")
    private String assigneeEmail;

    public String getTaskId() {
        return taskId;
    }

    public String getAssigneeEmail() {
        return assigneeEmail;
    }
}
