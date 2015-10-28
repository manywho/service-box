package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.Metadata;
import com.eclipsesource.json.JsonValue;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.enums.ContentType;
import com.manywho.services.box.facades.BoxFacade;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.List;

public class DatabaseSaveService {
    @Inject
    private BoxFacade boxFacade;

    @Inject
    private ObjectMapperService objectMapperService;

    public Object saveFileMetadata(String token, ObjectDataType objectDataType, Object metadataObject) throws Exception {
        // Get the virtual ___file property from the given Metadata object
        ObjectCollection fileObjects = metadataObject.getProperties().getObjectData("___file");
        if (CollectionUtils.isEmpty(fileObjects)) {
            throw new Exception("The metadata to save is missing an associated File");
        }

        String metadataType = objectDataType.getDeveloperName();

        // Fetch the file from Box, using the ID from the virtual ___file property
        BoxFile file = boxFacade.getFile(token, fileObjects.get(0).getExternalId());

        List<Metadata> fileMetadata = file.getAllMetadata();
        if (fileMetadata.stream().anyMatch(m -> m.getTemplate().equals(metadataType))) {
            // Get the correct metadata to update
            Metadata metadata = fileMetadata.stream().filter(m -> m.getTemplate().equals(metadataType))
                    .findFirst().get();

            // Perform the update to the Metadata on Box
            file.updateMetadata("enterprise", convertPropertiesToMetadata(metadataObject, metadata));
        } else {
            // Create a new Metadata item for the file on Box, with the given data
            file.createMetadata("enterprise/" + metadataType, convertPropertiesToMetadata(metadataObject, new Metadata()));
        }

        return objectMapperService.convertFileMetadata(file, objectDataType);
    }

    private Metadata convertPropertiesToMetadata(Object metadataObject, Metadata metadata) {
        metadataObject.getProperties().stream()
                .filter(property -> !property.getDeveloperName().equals("___file"))
                .filter(property -> !property.getDeveloperName().equals("___folder"))
                .forEach(property -> metadata.replace(
                        "/" + property.getDeveloperName(),
                        convertContentValueToMetadataValue(property.getContentType(), property.getContentValue())
                ));

        return metadata;
    }

    private JsonValue convertContentValueToMetadataValue(ContentType contentType, String contentValue) {
        switch (contentType) {
            case DateTime:
                return JsonValue.valueOf(contentValue);
            case Number:
                return JsonValue.valueOf(Float.parseFloat(contentValue));
            default:
                return JsonValue.valueOf(contentValue);
        }
    }
}
