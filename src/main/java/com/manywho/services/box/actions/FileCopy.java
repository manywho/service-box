package com.manywho.services.box.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;

public class FileCopy extends AbstractAction {
    @Override
    public String getUriPart() {
        return "file/copy";
    }

    @Override
    public String getDeveloperName() {
        return "File: Copy";
    }

    @Override
    public String getDeveloperSummary() {
        return "Copy a file into another folder, with an optional new name";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        DescribeValueCollection describeValues = new DescribeValueCollection();
        describeValues.add(new DescribeValue("Source File", ContentType.Object, true, null, File.NAME));
        describeValues.add(new DescribeValue("Destination Folder", ContentType.Object, true, null, Folder.NAME));
        describeValues.add(new DescribeValue("Name", ContentType.String, false));

        return describeValues;
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        return new DescribeValueCollection();
    }
}
