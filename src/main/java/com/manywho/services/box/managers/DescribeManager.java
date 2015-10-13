package com.manywho.services.box.managers;

import com.manywho.sdk.entities.describe.DescribeServiceRequest;
import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;
import com.manywho.services.box.services.DescribeService;

import javax.inject.Inject;

public class DescribeManager {
    @Inject
    private DescribeService describeService;

    public DescribeServiceResponse describe(DescribeServiceRequest describeRequest) throws Exception {
        String accessToken = "";
        if (describeRequest.hasConfigurationValues()) {
            accessToken = describeService.fetchEnterpriseAccessToken(describeRequest.getConfigurationValues());
        }

        return new DescribeServiceBuilder()
                .setProvidesIdentity(true)
                .setProvidesDatabase(true)
                .setProvidesFiles(true)
                .setProvidesLogic(true)
                .setCulture(new Culture("EN", "US"))
                .setConfigurationValues(new DescribeValueCollection() {{
                    add(new DescribeValue("Enterprise ID", ContentType.String, false));
                }})
                .setTypes(describeService.buildTypeElementsFromMetadataTemplates(accessToken))
                .createDescribeService()
                .createResponse();
    }
}
