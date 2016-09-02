package com.manywho.services.box.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.box.types.Task;
import com.manywho.services.box.types.TaskAssignment;

public class TaskAddAssignment extends AbstractAction {
    @Override
    public String getUriPart() {
        return "task/addassignment";
    }

    @Override
    public String getDeveloperName() {
        return "Task: Add Assignment";
    }

    @Override
    public String getDeveloperSummary() {
        return "Add an assignment to a task";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Task", ContentType.Object, true, null, Task.NAME));
            add(new DescribeValue("Assignee Email", ContentType.String, true));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("TaskAssignment", ContentType.Object, false, null, TaskAssignment.NAME));
        }};
    }
}
