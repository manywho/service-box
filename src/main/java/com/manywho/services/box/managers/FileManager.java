package com.manywho.services.box.managers;

import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.manywho.sdk.entities.run.elements.config.ServiceRequest;
import com.manywho.sdk.entities.run.elements.config.ServiceResponse;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.sdk.enums.InvokeType;
import com.manywho.sdk.services.PropertyCollectionParser;
import com.manywho.sdk.utils.StreamUtils;
import com.manywho.services.box.entities.actions.FileCopy;
import com.manywho.services.box.entities.actions.FileMove;
import com.manywho.services.box.client.BoxClient;
import com.manywho.services.box.services.FileService;
import com.manywho.services.box.services.FileUploadService;
import com.manywho.services.box.services.ObjectMapperService;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;
import java.util.stream.Collectors;

public class FileManager {

    @Inject
    private FileService fileService;

    @Inject
    private FileUploadService fileUploadService;

    @Inject
    private PropertyCollectionParser propertyParser;

    @Inject
    private BoxClient boxClient;

    @Inject
    private ObjectMapperService objectMapperService;

    public ObjectDataResponse uploadFile(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileUploadService.getFilePart(formDataMultiPart);

        if (bodyPart != null) {
            BoxFile.Info fileInformation = fileUploadService.uploadFileToBox(authenticatedWho.getToken(), fileDataRequest, bodyPart);

            if (fileInformation != null) {

                return new ObjectDataResponse(fileService.buildManyWhoFileObject(fileInformation, fileInformation.getResource()));
            }
        }

        throw new Exception("A file was not provided to upload to Box");
    }

    public ObjectCollection loadFiles(AuthenticatedWho authenticatedWho, String resourcePath) {
        // Loop over all the files in the loaded folder, and convert them to Box File Service Type
        return StreamUtils.asStream(findFolder(authenticatedWho.getToken(), resourcePath)
                .getChildren(BoxFile.ALL_FIELDS).iterator())
                .filter(i -> i instanceof BoxFile.Info)
                .map(f -> objectMapperService.convertBoxFile((BoxFile.Info) f, null))
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    public ObjectCollection loadManyWhoFiles(AuthenticatedWho authenticatedWho, String resourcePath, FileListFilter fileFilter) {
        // Loop over all the files in the loaded folder, and convert them to $File objects
        return StreamUtils.asStream(findFolder(authenticatedWho.getToken(), resourcePath)
                .getChildrenRange(fileFilter.getOffset(), fileFilter.getLimit(), BoxFile.ALL_FIELDS).iterator())
                .filter(i -> i instanceof BoxFile.Info)
                .map(f -> objectMapperService.convertToManyWhoFile((BoxFile.Info) f, null))
                .collect(Collectors.toCollection(ObjectCollection::new));
    }

    private BoxFolder findFolder(String token,String resourcePath) {
        BoxFolder folder = boxClient.getFolder(token, resourcePath);

        if (folder == null) {
            throw new RuntimeException("A folder could not be found with the ID " + resourcePath);
        }
        return folder;
    }


    public ObjectCollection loadManyWhoFile(AuthenticatedWho authenticatedWho, String fileId) {
        BoxFile file = boxClient.getFile(authenticatedWho.getToken(), fileId);

        ObjectCollection files = new ObjectCollection();
        files.add(fileService.buildManyWhoFileObject(file.getInfo(), file));

        return files;
    }

    public ServiceResponse copyFile(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        FileCopy fileCopy = propertyParser.parse(serviceRequest.getInputs(), FileCopy.class);
        if (fileCopy == null) {
            throw new Exception("Unable to parse the incoming FileCopy request");
        }

        fileService.copyFile(user.getToken(), fileCopy.getFile().getId(), fileCopy.getFolder().getId(), fileCopy.getName());

        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }

    public ServiceResponse moveFile(AuthenticatedWho user, ServiceRequest serviceRequest) throws Exception {
        FileMove fileMove = propertyParser.parse(serviceRequest.getInputs(), FileMove.class);
        if (fileMove == null) {
            throw new Exception("Unable to parse the incoming FileMove request");
        }

        fileService.moveFile(user.getToken(), fileMove.getFile().getId(), fileMove.getFolder().getId(), fileMove.getName());

        return new ServiceResponse(InvokeType.Forward, serviceRequest.getToken());
    }
}
