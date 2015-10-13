package com.manywho.services.box.services;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxDeveloperEditionAPIConnection;
import com.box.sdk.BoxMetadataTemplate;
import com.box.sdk.EncryptionAlgorithm;
import com.manywho.sdk.entities.draw.elements.type.TypeElement;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.box.configuration.SecurityConfiguration;
import com.manywho.services.box.entities.Configuration;
import com.manywho.services.box.oauth2.BoxProvider;
import com.manywho.services.box.types.File;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DescribeService {
    @Inject
    private PropertyCollectionParser propertyParser;

    @Inject
    private SecurityConfiguration securityConfiguration;

    public TypeElementCollection buildTypeElementsFromMetadataTemplates(String accessToken) {
        // If no access token is provided, then don't try and create Types from Metadata templates
        if (StringUtils.isEmpty(accessToken)) {
            return new TypeElementCollection();
        }

        TypeElementCollection typeElements = new TypeElementCollection();

        BoxAPIConnection apiConnection = new BoxAPIConnection(accessToken);

        List<BoxMetadataTemplate.Info> templates = BoxMetadataTemplate.getEnterpriseTemplates(apiConnection);
        for (BoxMetadataTemplate.Info template : templates) {
            TypeElement.SimpleTypeBuilder typeBuilder = new TypeElement.SimpleTypeBuilder()
                    .setDeveloperName("Metadata: " + template.getDisplayName())
                    .setTableName(template.getTemplateKey());

            // Generate all the properties and bindings for any non-enum fields
            template.getFields().stream()
                    .filter(field -> !field.getType().equals("enum"))
                    .forEach(field -> typeBuilder.addProperty(field.getDisplayName(), convertToContentType(field.getType()), field.getKey()));

            // Add the virtual "file" field, for use in database loads
            typeBuilder.addProperty("File", ContentType.Object, File.NAME, "___file");

            typeElements.add(typeBuilder.build());
        }

        return typeElements;
    }

    public String fetchEnterpriseAccessToken(EngineValueCollection configurationValues) throws Exception {
        String privateKey = new String(Files.readAllBytes(Paths.get(securityConfiguration.getPrivateKeyLocation())));

        Configuration configuration = propertyParser.parse(configurationValues, Configuration.class);

        return BoxDeveloperEditionAPIConnection.getAppEnterpriseConnection(
                configuration.getEnterpriseId(),
                BoxProvider.CLIENT_ID,
                BoxProvider.CLIENT_SECRET,
                privateKey,
                securityConfiguration.getPrivateKeyPassword(),
                EncryptionAlgorithm.RSA_SHA_256
        ).getAccessToken();
    }

    private static ContentType convertToContentType(String type) {
        switch (type) {
            case "date":
                return ContentType.DateTime;
            case "enum":
                return ContentType.String;
            case "float":
                return ContentType.Number;
            case "string":
                return ContentType.String;
            default:
                return ContentType.String;
        }
    }
}
