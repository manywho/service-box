package com.manywho.services.box.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;

public class GetVerificationToken extends AbstractAction {
    @Override
    public String getUriPart() {
        return "token/verification";
    }

    @Override
    public String getDeveloperName() {
        return "Create A Verification Token";
    }

    @Override
    public String getDeveloperSummary() {
        return "Used to work with metadata";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection();
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        DescribeValueCollection describeValues = new DescribeValueCollection();
        describeValues.add(new DescribeValue("Verification Token", ContentType.String, false));

        return describeValues;
    }
}
