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
        DescribeValueCollection describeValues = new DescribeValueCollection();
        describeValues.add(new DescribeValue("Task", ContentType.Object, true, null, Task.NAME));
        describeValues.add(new DescribeValue("Assignee Email", ContentType.String, true));

        return describeValues;
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        DescribeValueCollection describeValues = new DescribeValueCollection();
        describeValues.add(new DescribeValue("Task Assignment", ContentType.Object, false, null, TaskAssignment.NAME));

        return describeValues;
    }
}
