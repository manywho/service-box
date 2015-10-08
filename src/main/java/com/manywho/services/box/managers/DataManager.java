package com.manywho.services.box.managers;

import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.CriteriaType;
import com.manywho.services.box.services.DatabaseLoadService;
import com.manywho.services.box.services.DatabaseSaveService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Optional;

public class DataManager {
    @Inject
    private DatabaseLoadService databaseLoadService;

    @Inject
    private DatabaseSaveService databaseSaveService;

    public ObjectCollection loadFileType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadFile(user.getToken(), objectDataRequest.getListFilter().getId()));
        }

        return databaseLoadService.loadFiles(user.getToken());
    }

    public ObjectCollection loadMetadataType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check to see if we're filtering by file - if so, load only a single object
        if (objectDataRequest.getListFilter().getWhere().stream().anyMatch(w -> w.getColumnName().equals("___file"))) {
            Optional<ListFilterWhere> fileWhereOptional = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(w -> w.getColumnName().equals("___file"))
                    .filter(w -> w.getCriteriaType().equals(CriteriaType.Equal))
                    .findFirst();

            if (!fileWhereOptional.isPresent()) {
                throw new Exception("Searching for metadata by filtering with a file only works with EQUAL criteria");
            }

            return new ObjectCollection(databaseLoadService.loadSingleMetadata(
                user.getToken(),
                objectDataRequest.getObjectDataType(),
                fileWhereOptional.get()
            ));
        }

        return databaseLoadService.loadMetadata(user.getToken(), objectDataRequest.getObjectDataType());
    }

    public Object saveMetadataType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        return databaseSaveService.saveFileMetadata(
                user.getToken(),
                objectDataRequest.getObjectDataType(),
                objectDataRequest.getObjectData().get(0)
        );
    }
}
