package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.Metadata;
import com.manywho.sdk.entities.run.elements.type.MObject;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.enums.ContentType;
import com.manywho.services.box.client.BoxClient;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;

public class DatabaseSaveService {
    private BoxClient boxFacade;
    private ObjectMapperService objectMapperService;

    @Inject
    public DatabaseSaveService(BoxClient boxFacade, ObjectMapperService objectMapperService) {
        this.boxFacade = boxFacade;
        this.objectMapperService = objectMapperService;
    }

    public Object saveFileMetadata(String token, ObjectDataType objectDataType, MObject metadataObject) throws Exception {
        // Get the virtual ___file property from the given Metadata object
        ObjectCollection fileObjects = metadataObject.getProperties().getObjectData("___file");
        if (CollectionUtils.isEmpty(fileObjects)) {
            throw new Exception("The metadata to save is missing an associated File");
        }

        String metadataType = objectDataType.getDeveloperName();

        // Fetch the file from Box, using the ID from the virtual ___file property
        BoxFile file = boxFacade.getFile(token, fileObjects.get(0).getExternalId());

        Iterable<Metadata> fileMetadata = file.getAllMetadata();
        Metadata metadata1 = null;

        for (Metadata metadata:fileMetadata) {
            if(metadata.getTemplateName().equals(metadataType)) {
                metadata1 = metadata;
                file.updateMetadata(convertPropertiesToMetadata(metadataObject, metadata));
            }
        }

        if (metadata1 == null){
            // Create a new Metadata item for the file on Box, with the given data
            file.createMetadata(metadataType, convertPropertiesToMetadata(metadataObject, new Metadata()));
        }

        return objectMapperService.convertFileMetadata(file, objectDataType);
    }

    private Metadata convertPropertiesToMetadata(MObject metadataObject, Metadata metadata) {
        metadataObject.getProperties().stream()
                .filter(property -> !property.getDeveloperName().equals("___file"))
                .filter(property -> !property.getDeveloperName().equals("___folder"))
                .forEach(property -> metadata.replace(
                        "/" + property.getDeveloperName(),
                        convertContentValueToMetadataValue(property.getContentType(), property.getContentValue())
                ));

        return metadata;
    }

    private String convertContentValueToMetadataValue(ContentType contentType, String contentValue) {
        switch (contentType) {
            case DateTime:
                return String.valueOf(contentValue);
            case Number:
                return String.valueOf(Float.parseFloat(contentValue));
            default:
                return String.valueOf(contentValue);
        }
    }
}
