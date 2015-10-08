package com.manywho.services.box.managers;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxFile;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.manywho.sdk.entities.run.elements.type.*;
import com.manywho.sdk.entities.security.AuthenticatedWho;
import com.manywho.services.box.services.FileService;
import com.manywho.services.box.services.FileUploadService;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;

public class FileManager {
    @Inject
    private FileService fileService;

    @Inject
    private FileUploadService fileUploadService;

    public ObjectDataResponse uploadFile(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest, FormDataMultiPart formDataMultiPart) throws Exception {
        BodyPart bodyPart = fileUploadService.getFilePart(formDataMultiPart);
        if (bodyPart != null) {
            BoxFile.Info fileInformation = fileUploadService.uploadFileToBox(authenticatedWho.getToken(), fileDataRequest, bodyPart);
            if (fileInformation != null) {
                return new ObjectDataResponse(fileService.buildManyWhoFileObject(fileInformation));
            }
        }

        throw new Exception("A file was not provided to upload to Box");
    }

    public ObjectDataResponse loadFiles(AuthenticatedWho authenticatedWho, FileDataRequest fileDataRequest) {
        String selectedFolder = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "0";

        BoxAPIConnection apiConnection = new BoxAPIConnection(authenticatedWho.getToken());
        BoxFolder folder = new BoxFolder(apiConnection, selectedFolder);

        ObjectCollection files = new ObjectCollection();

        for (BoxItem.Info itemInfo : folder) {
            if (itemInfo instanceof BoxFile.Info) {
                files.add(fileService.buildManyWhoFileObject((BoxFile.Info) itemInfo));
            }
        }

        return new ObjectDataResponse(files);
    }
}
