package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.manywho.sdk.entities.run.elements.type.Object;
import com.manywho.sdk.entities.run.elements.type.ObjectCollection;
import com.manywho.sdk.entities.run.elements.type.ObjectDataType;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.entities.MetadataSearch;
import com.manywho.services.box.facades.BoxFacade;
import org.apache.commons.lang3.ArrayUtils;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class DatabaseLoadService {
    private static final int MAX_FILE_SIZE_FOR_LOADING_CONTENT = 100000;

    @Inject
    private BoxFacade boxFacade;

    @Inject
    private ObjectMapperService objectMapperService;

    public Object loadFile(String token, String id) throws Exception {
        BoxFile file = boxFacade.getFile(token, id);
        if (file != null) {
            BoxFile.Info info = file.getInfo(BoxFile.ALL_FIELDS);

            return objectMapperService.convertBoxFile(info, loadFileContent(info));
        }

        throw new Exception("Unable to load file with ID " + id + " from Box");
    }

    public Object loadFolder(String token, String id) throws Exception {
        BoxFolder folder = boxFacade.getFolder(token, id);
        if (folder != null) {
            return objectMapperService.convertBoxFolder(folder.getInfo());
        }

        throw new Exception("Unable to load folder with ID " + id + " from Box");
    }

    public ObjectCollection loadMetadata(String token, ObjectDataType objectDataType, MetadataSearch metadataSearch) {
        Iterable<BoxItem.Info> files = boxFacade.searchByMetadata(token, objectDataType.getDeveloperName(), metadataSearch);

        // Create an object based on the given metadata type for all the result, using the objectDataType passed in
        return StreamUtils.asStream(files.iterator())
                .filter(i -> i instanceof BoxFile.Info)
                .map(f -> objectMapperService.convertFileMetadata(((BoxFile.Info) f).getResource(), objectDataType))
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    public ObjectCollection loadSingleMetadata(String token, ObjectDataType objectDataType, String id) throws Exception {
        // Load the file given in the filter from Box
        BoxFile file = boxFacade.getFile(token, id);
        if (file == null) {
            throw new Exception("A file could not be found for with the ID " + id);
        }

        // Create a ManyWho object based on the given metadata ObjectDataType and the loaded file
        return new ObjectCollection(objectMapperService.convertFileMetadata(file, objectDataType));
    }

    public ObjectCollection loadFiles(String token, String folderId) throws Exception {
        BoxFolder folder = boxFacade.getFolder(token, folderId);
        if (folder == null) {
            throw new Exception("A folder could not be found with the ID " + folderId);
        }

        // Loop over all the files in the loaded folder, and convert them to ManyWho objects
        return StreamUtils.asStream(folder.getChildren(BoxFile.ALL_FIELDS).iterator())
                .filter(i -> i instanceof BoxFile.Info)
                .map(f -> objectMapperService.convertBoxFileBasic((BoxFile.Info) f))
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    private String loadFileContent(BoxFile.Info fileInfo) {
        String[] textExtensions = new String[] { "txt", "py", "js", "xml", "css", "md", "pl", "php", "c", "m", "json" };

        // Only load the content of the file if it's smaller than 100kb and a text file
        if (ArrayUtils.contains(textExtensions, fileInfo.getExtension()) && fileInfo.getSize() <= MAX_FILE_SIZE_FOR_LOADING_CONTENT) {
            ByteArrayOutputStream contentStream = new ByteArrayOutputStream();
            fileInfo.getResource().download(contentStream);

            return new String(contentStream.toByteArray(), StandardCharsets.UTF_8);
        }

        return null;
    }
}
