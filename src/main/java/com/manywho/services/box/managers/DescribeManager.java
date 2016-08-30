package com.manywho.services.box.managers;

import com.manywho.sdk.entities.describe.DescribeServiceRequest;
import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;
import com.manywho.services.box.entities.Configuration;
import com.manywho.services.box.services.DescribeService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;

public class DescribeManager {
    @Inject
    private DescribeService describeService;

    @Inject
    private PropertyCollectionParser propertyParser;

    public DescribeServiceResponse describe(DescribeServiceRequest describeRequest) throws Exception {
        String accessToken = "";
        if (describeRequest.hasConfigurationValues()) {
            EngineValueCollection configurationValues = describeRequest.getConfigurationValues();
            Configuration configuration = propertyParser.parse(configurationValues, Configuration.class);

            if (!StringUtils.isEmpty(configuration.getEnterpriseId())) {
                accessToken = describeService.fetchEnterpriseAccessToken(configuration);
            }
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
