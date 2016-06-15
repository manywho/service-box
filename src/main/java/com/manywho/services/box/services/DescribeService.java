package com.manywho.services.box.services;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxMetadataTemplate;
import com.manywho.sdk.entities.draw.elements.type.TypeElement;
import com.manywho.sdk.entities.draw.elements.type.TypeElementCollection;
import com.manywho.sdk.entities.run.EngineValueCollection;
import com.manywho.sdk.enums.ContentType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.services.box.entities.Configuration;
import com.manywho.services.box.facades.BoxFacade;
import com.manywho.services.box.types.File;
import com.manywho.services.box.types.Folder;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.List;

public class DescribeService {
    @Inject
    private PropertyCollectionParser propertyParser;

    @Inject
    private BoxFacade boxFacade;

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

            // Generate all the properties and bindings for the fields
            template.getFields().stream()
                    .forEach(field -> typeBuilder.addProperty(field.getDisplayName(), convertToContentType(field.getType()), field.getKey()));

            // Add the virtual "file" and "folder" fields, for use in database loads
            typeBuilder.addProperty("File", ContentType.Object, File.NAME, "___file");
            typeBuilder.addProperty("Folder", ContentType.Object, Folder.NAME, "___folder");

            typeElements.add(typeBuilder.build());
        }

        return typeElements;
    }

    public String fetchEnterpriseAccessToken(EngineValueCollection configurationValues) throws Exception {
        Configuration configuration = propertyParser.parse(configurationValues, Configuration.class);

        return boxFacade.createDeveloperApiConnection(configuration.getEnterpriseId()).getAccessToken();
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
