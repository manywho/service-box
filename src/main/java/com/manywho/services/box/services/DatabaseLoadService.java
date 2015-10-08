package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.Metadata;
import com.manywho.sdk.entities.run.elements.type.ListFilterWhere;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.entities.run.elements.type.ObjectDataTypeProperty;
import com.manywho.sdk.entities.run.elements.type.Property;
import com.manywho.sdk.entities.run.elements.type.PropertyCollection;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.facades.BoxFacade;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class DatabaseLoadService {
    @Inject
    private BoxFacade boxFacade;

    @Inject
    private ObjectMapperService objectMapperService;

    public Object loadFile(String token, String id) throws Exception {
        BoxFile file = boxFacade.getFile(token, id);
        if (file != null) {
            return objectMapperService.convertBoxFile(file.getInfo());
        }

        throw new Exception("Unable to load file with ID " + id + " from Box");
    }

    public ObjectCollection loadMetadata(String token, ObjectDataType objectDataType) {
        ObjectCollection boxObjects = new ObjectCollection();

        Iterable<BoxItem.Info> files = boxFacade.searchByMetadata(token, objectDataType.getDeveloperName());
        if (files.iterator().hasNext()) {
            BoxFile.Info info = (BoxFile.Info) files.iterator().next();

            BoxFile file = boxFacade.getFile(token, info.getID());
            if (file != null) {
                // Create an object based on the given metadata type (use the objectDataType passed in)
                boxObjects.add(objectMapperService.convertFileMetadata(file, objectDataType));
            }
        }

        return boxObjects;
    }

    public Object loadSingleMetadata(String token, ObjectDataType objectDataType, ListFilterWhere fileWhere) throws Exception {
        // Load the file given in the filter from Box
        BoxFile file = boxFacade.getFile(token, fileWhere.getObjectData().get(0).getExternalId());
        if (file == null) {
            throw new Exception("A file could not be found for with the ID " + fileWhere.getObjectData().get(0).getExternalId());
        }

        // Create a ManyWho object based on the given metadata ObjectDataType and the loaded file
        return objectMapperService.convertFileMetadata(file, objectDataType);
    }

    public ObjectCollection loadFiles(String token) throws Exception {
        BoxFolder folder = boxFacade.getFolder(token, "0");
        if (folder == null) {
            throw new Exception("A folder could not be found with the ID " + "0");
        }

        return StreamUtils.asStream(folder.iterator())
                .filter(i -> i instanceof BoxFile.Info)
                .map(f -> objectMapperService.convertBoxFile((BoxFile.Info) f))
                .collect(Collectors.toCollection(ObjectCollection::new));
    }
}
