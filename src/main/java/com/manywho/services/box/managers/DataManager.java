package com.manywho.services.box.managers;

import com.box.sdk.BoxSearchParameters;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.CriteriaType;
import com.manywho.services.box.entities.MetadataSearch;
import com.manywho.services.box.services.DatabaseLoadService;
import com.manywho.services.box.services.DatabaseSaveService;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DataManager {
    @Inject
    private DatabaseLoadService databaseLoadService;

    @Inject
    private DatabaseSaveService databaseSaveService;

    @Inject
    private FileManager fileManager;

    public ObjectCollection loadFileType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadFile(user.getToken(), objectDataRequest.getListFilter().getId()));
        }

        // Try and get the folder to search in, if one was passed in as a filter otherwise use "0" (the root folder)
        String boxFolderId = "0";
        if (objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere() != null) {
            ListFilterWhere parentFolderFilter = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(w -> w.getColumnName().equals("Parent Folder"))
                    .findFirst()
                    .orElse(new ListFilterWhere());

            if (parentFolderFilter.getObjectData() != null && parentFolderFilter.getObjectData().size() > 0) {
                boxFolderId = parentFolderFilter.getObjectData().get(0).getProperties().stream()
                        .filter(property -> property.getDeveloperName().equals("ID"))
                        .map(property -> property.getContentValue())
                        .findFirst()
                        .orElse("0");
            }
        }

        return databaseLoadService.loadFiles(user.getToken(), boxFolderId, objectDataRequest.getListFilter());
    }

    public ObjectCollection loadFileSystem(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return fileManager.loadManyWhoFile(user, objectDataRequest.getListFilter().getId());
        }

        // at the moment only support list of the files for the root directory
        String folderId = "0";

        return fileManager.loadFiles(user, folderId);
    }

    public ObjectCollection loadFolderType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadFolder(user.getToken(), objectDataRequest.getListFilter().getId()));
        }

        if(objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere() != null) {

            BoxSearchParameters boxSearchParameters = new BoxSearchParameters();
            boxSearchParameters.setType("folder");
            ListFilterWhereCollection listFilterWheres = objectDataRequest.getListFilter().getWhere();

            List<String> contentTypes = new ArrayList<>();
            List<String> ancestors = new ArrayList<>();

            Boolean validFilter = false;

            for (ListFilterWhere where : listFilterWheres) {
                if (Objects.equals(where.getColumnName(), "Name")) {
                    boxSearchParameters.setQuery(where.getContentValue());
                    contentTypes.add("name");
                    validFilter = true;
                }
                if (Objects.equals(where.getColumnName(), "Description")) {
                    boxSearchParameters.setQuery(where.getContentValue());
                    contentTypes.add("description");
                    validFilter = true;
                }

                if (Objects.equals(where.getColumnName(), "Parent Folder")) {
                    ancestors.add(where.getContentValue());
                    boxSearchParameters.setAncestorFolderIds(ancestors);
                    validFilter = true;
                }
            }

            if (StringUtils.isEmpty(objectDataRequest.getListFilter().getSearch()) == false) {
                boxSearchParameters.setQuery(objectDataRequest.getListFilter().getSearch());
                validFilter = true;
            }

            if(!validFilter) {
                return databaseLoadService.loadRootFolders(user.getToken(), objectDataRequest.getListFilter());
            }

            boxSearchParameters.setContentTypes(contentTypes);

            return databaseLoadService.loadFolder(user.getToken(), boxSearchParameters, objectDataRequest.getListFilter());
        }

        throw new Exception("Loading a list of folders is not yet supported");
    }


    public ObjectCollection loadTask(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        String fileId = getPropertyValue(objectDataRequest.getListFilter(),"File", "ID");

        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadTask(user.getToken(), objectDataRequest.getListFilter().getId()));
        } else if (StringUtils.isNotEmpty(fileId)) {
            return databaseLoadService.loadTasksByFile(user.getToken(), fileId);
        }

        throw new Exception("Loading a list of task is only supported if it is filtered by unique ID or valid File filter");
    }

    private String getPropertyValue(ListFilter listFilter, String objectDeveloperName, String searchingField) {
        ObjectCollection objects = getObjectData(listFilter);
        MObject object = objects.stream().filter(o -> o.getDeveloperName().equals(objectDeveloperName))
                .findFirst().orElse(new MObject(objectDeveloperName,"", new PropertyCollection()));

        return object.getProperties().stream()
                .filter(property -> property.getDeveloperName().equals(searchingField))
                .findFirst()
                .map( property -> property.getContentValue())
                .orElse(null);
    }

    private ObjectCollection getObjectData(ListFilter listFilter) {

        if (listFilter != null && listFilter.getWhere() != null && listFilter.getWhere().size() > 0 &&
                listFilter.getWhere().get(0).getObjectData() != null && listFilter.getWhere().get(0).getObjectData().size() > 0) {

            return listFilter.getWhere().get(0).getObjectData();
        }

        return new ObjectCollection();
    }

    public ObjectCollection loadTaskAssignment(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadTaskAssignment(user.getToken(), objectDataRequest.getListFilter().getId()));
        }

        throw new Exception("Loading a list of task is not yet supported");
    }

    public ObjectCollection loadMetadataType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        MetadataSearch metadataSearch = new MetadataSearch();

        if (objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere()!= null) {
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

        return databaseLoadService.loadMetadata(user.getToken(), objectDataRequest, metadataSearch);
    }

    public Object saveMetadataType(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        return databaseSaveService.saveFileMetadata(
                user.getToken(),
                objectDataRequest.getObjectDataType(),
                objectDataRequest.getObjectData().get(0)
        );
    }

    public ObjectCollection loadComments(AuthenticatedWho user, ObjectDataRequest objectDataRequest) throws Exception {
        // Check if the load is for a single object with an identifier
        if (objectDataRequest.getListFilter() != null && StringUtils.isNotEmpty(objectDataRequest.getListFilter().getId())) {
            return new ObjectCollection(databaseLoadService.loadComment(user.getToken(), objectDataRequest.getListFilter().getId()));
        }

        // Try and get the folder to search in, if one was passed in as a filter otherwise use "0" (the root folder)
        String file = "0";
        if (objectDataRequest.getListFilter() != null && objectDataRequest.getListFilter().getWhere() != null) {
            Optional<ListFilterWhere> fileFilter = objectDataRequest.getListFilter().getWhere().stream()
                    .filter(w -> w.getColumnName().equals("File"))
                    .findFirst();

            if (fileFilter.isPresent()) {
                file = fileFilter.get().getObjectData().get(0).getExternalId();
            }
        }

        return databaseLoadService.loadComments(user.getToken(), file);
    }
}
