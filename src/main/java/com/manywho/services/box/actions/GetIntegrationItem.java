package com.manywho.services.box.actions;

import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.actions.AbstractAction;
import com.manywho.services.box.types.Item;

public class GetIntegrationItem extends AbstractAction {
    @Override
    public String getUriPart() {
        return "integration/source-item";
    }

    @Override
    public String getDeveloperName() {
        return "Fetch Integration Item";
    }

    @Override
    public String getDeveloperSummary() {
        return "Fetch the item used to execute the flow by the integration tool";
    }

    @Override
    public DescribeValueCollection getServiceInputs() {
        return new DescribeValueCollection();
    }

    @Override
    public DescribeValueCollection getServiceOutputs() {
        DescribeValueCollection describeValues = new DescribeValueCollection();
        describeValues.add(new DescribeValue(Item.NAME, ContentType.Object, false, null, Item.NAME));

        return describeValues;
    }
}
