package com.manywho.services.box.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.box.types.Folder;

public class FolderCreate extends AbstractAction {
    @Override
    public String getUriPart() {
        return "folder/create";
    }

    @Override
    public String getDeveloperName() {
        return "Folder: Create";
    }

    @Override
    public String getDeveloperSummary() {
        return "Create a new folder";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Parent Folder", ContentType.Object, true, null, Folder.NAME));
            add(new DescribeValue("Name", ContentType.String, false));
        }};
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection() {{
            add(new DescribeValue("Folder", ContentType.Object, false, null, Folder.NAME));
        }};
    }
}
