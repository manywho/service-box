package com.manywho.services.box.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Task;

public class TaskCreate extends AbstractAction {
    @Override
    public String getUriPart() {
        return "task/create";
    }

    @Override
    public String getDeveloperName() {
        return "Task: Create";
    }

    @Override
    public String getDeveloperSummary() {
        return "Create a task for a file";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("File", ContentType.Object, true, null, File.NAME));
            add(new DescribeValue("Message", ContentType.String, false));
            add(new DescribeValue("Due At", ContentType.DateTime, false));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Task", ContentType.Object, true, null, Task.NAME));
        }};
    }
}
