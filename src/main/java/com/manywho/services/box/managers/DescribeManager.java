package com.manywho.services.box.managers;

import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;
import com.manywho.services.box.services.DescribeService;

import javax.inject.Inject;

public class DescribeManager {
    @Inject
    private DescribeService describeService;

    public DescribeServiceResponse describe() throws Exception {
        // Replace this with an OAuth2 access token if you want Types to be created from Metadata templates
        String accessToken = "";

        return new DescribeServiceBuilder()
                .setProvidesIdentity(true)
                .setProvidesDatabase(true)
                .setProvidesFiles(true)
                .setProvidesLogic(true)
                .setCulture(new Culture("EN", "US"))
                .setTypes(describeService.buildTypeElementsFromMetadataTemplates(accessToken))
                .createDescribeService()
                .createResponse();
    }
}
