package com.manywho.services.box.managers;

import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxUser;
import com.google.common.base.Strings;
import com.manywho.sdk.entities.describe.DescribeServiceRequest;
import com.manywho.sdk.entities.describe.DescribeServiceResponse;
import com.manywho.sdk.entities.describe.DescribeValue;
import com.manywho.sdk.entities.describe.DescribeValueCollection;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.entities.translate.Culture;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.services.describe.DescribeServiceBuilder;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.entities.Configuration;
import com.manywho.services.box.services.DescribeService;
import com.manywho.services.box.services.EncryptService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.jose4j.lang.JoseException;

import javax.inject.Inject;
import java.io.IOException;

public class DescribeManager {
    @Inject
    private DescribeService describeService;

    @Inject
    private BoxClient boxClient;

    @Inject
    private PropertyCollectionParser propertyParser;

    @Inject
    private EncryptService encryptService;

    private static final Logger LOGGER = LogManager.getLogger(new ParameterizedMessageFactory());

    public DescribeServiceResponse describe(DescribeServiceRequest describeRequest) throws Exception {
        String accessToken = "";
        if (describeRequest.hasConfigurationValues()) {
            EngineValueCollection configurationValues = describeRequest.getConfigurationValues();
            Configuration configuration = propertyParser.parse(configurationValues, Configuration.class);

            if (isUserInEnterprise(configuration.getEnterpriseId(), configuration.getVerificationToken())) {
                accessToken = describeService.fetchEnterpriseAccessToken(configuration);
            }
        }

        DescribeValueCollection describeValues = new DescribeValueCollection();
        describeValues.add(new DescribeValue("Enterprise ID", ContentType.String, false));
        describeValues.add(new DescribeValue("Verification Token", ContentType.String, false));

        return new DescribeServiceBuilder()
                .setProvidesIdentity(true)
                .setProvidesDatabase(true)
                .setProvidesFiles(true)
                .setProvidesLogic(true)
                .setCulture(new Culture("EN", "US"))
                .setConfigurationValues(describeValues)
                .setTypes(describeService.buildTypeElementsFromMetadataTemplates(accessToken))
                .createDescribeService()
                .createResponse();
    }

    private boolean isUserInEnterprise(String enterpriseId, String verificationToken) throws JoseException, IOException {
        if (Strings.isNullOrEmpty(enterpriseId) || Strings.isNullOrEmpty(verificationToken)) {
            return false;
        }
        String decryptedVerificationToken = encryptService.decryptData(verificationToken);

        BoxUser.Info boxUser = boxClient.getCurrentUser(decryptedVerificationToken);
        BoxDeveloperEditionAPIConnection apiConnection = boxClient.createDeveloperApiConnection(enterpriseId);
        Iterable<BoxUser.Info> users = boxClient.loadUsers(apiConnection.getAccessToken());

        return StreamUtils.asStream(users.iterator())
                .anyMatch(u -> u.getID().equals(boxUser.getID()));

    }
}
