package com.manywho.services.box.managers;

import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.CriteriaType;
import com.manywho.services.box.entities.MetadataSearch;
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

        // Try and get the folder to search in, if one was passed in as a filter otherwise use "0" (the root folder)
        String folder = "0";
        if (objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere() != null) {
            Optional<ListFilterWhere> folderFilter = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(w -> w.getColumnName().equals("Parent Folder"))
                    .findFirst();

            if (folderFilter.isPresent()) {
                folder = folderFilter.get().getObjectData().get(0).getExternalId();
            }
        }

        return databaseLoadService.loadFiles(user.getToken(), folder);
    }

    public ObjectCollection loadFolderType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadFolder(user.getToken(), objectDataRequest.getListFilter().getId()));
        }

        throw new Exception("Loading a list of folders is not yet supported");
    }

    public ObjectCollection loadMetadataType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        MetadataSearch metadataSearch = new MetadataSearch();

        if (objectDataRequest.getListFilter() != null) {
            // Add any columns in the filter to the metadata search object
            objectDataRequest.getListFilter().getWhere().stream()
                    .filter(where -> !where.getColumnName().equals("___file"))
                    .filter(where -> !where.getColumnName().equals("___folder"))
                    .forEach(where -> metadataSearch.addField(where.getColumnName(), where.getContentValue()));

            // Check to see if we're filtering by folder
            if (objectDataRequest.getListFilter().getWhere().stream().anyMatch(w -> w.getColumnName().equals("___folder"))) {
                Optional<ListFilterWhere> folderWhereOptional = objectDataRequest.getListFilter().getWhere().stream()
                        .filter(w -> w.getColumnName().equals("___folder"))
                        .filter(w -> w.getCriteriaType().equals(CriteriaType.Equal))
                        .findFirst();

                if (folderWhereOptional.isPresent()) {
                    metadataSearch.setFolderId(folderWhereOptional.get().getObjectData().get(0).getExternalId());
                } else {
                    throw new Exception("Searching for metadata by filtering with a folder only works with EQUAL criteria");
                }
            }

            // Check to see if we're filtering by file - if so, load only a single object
            if (objectDataRequest.getListFilter().getWhere().stream().anyMatch(w -> w.getColumnName().equals("___file"))) {
                Optional<ListFilterWhere> fileWhereOptional = objectDataRequest.getListFilter().getWhere().stream()
                        .filter(w -> w.getColumnName().equals("___file"))
                        .filter(w -> w.getCriteriaType().equals(CriteriaType.Equal))
                        .findFirst();

                if (fileWhereOptional.isPresent()) {
                    metadataSearch.setFileId(fileWhereOptional.get().getObjectData().get(0).getExternalId());
                } else {
                    throw new Exception("Searching for metadata by filtering with a file only works with EQUAL criteria");
                }
            }
        }

        // Check if we have a file ID - if so, then only return one record
        if (StringUtils.isNotEmpty(metadataSearch.getFileId())) {
            return databaseLoadService.loadSingleMetadata(user.getToken(), objectDataRequest.getObjectDataType(), metadataSearch.getFileId());
        }

        return databaseLoadService.loadMetadata(user.getToken(), objectDataRequest.getObjectDataType(), metadataSearch);
    }

    public Object saveMetadataType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        return databaseSaveService.saveFileMetadata(
                user.getToken(),
                objectDataRequest.getObjectDataType(),
                objectDataRequest.getObjectData().get(0)
        );
    }
}
