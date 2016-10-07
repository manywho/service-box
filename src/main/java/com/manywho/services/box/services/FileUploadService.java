package com.manywho.services.box.services;

import com.box.sdk.BoxFile;
import com.manywho.sdk.entities.run.elements.type.FileDataRequest;
import com.manywho.services.box.client.BoxClient;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.BodyPartEntity;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class FileUploadService {
    private BoxClient boxClient;

    @Inject
    public FileUploadService(BoxClient boxClient){
        this.boxClient = boxClient;
    }

    public BodyPart getFilePart(FormDataMultiPart formDataMultiPart) throws Exception {
        // If the filename is blank or doesn't exist, assume it's the FileDataRequest and skip it
        Optional<BodyPart> filePart = formDataMultiPart.getBodyParts().stream()
                .filter(bodyPart -> StringUtils.isNotEmpty(bodyPart.getContentDisposition().getFileName()))
                .findFirst();

        if (filePart.isPresent()) {
            return filePart.get();
        }

        throw new Exception("A file could not be found in the received request");
    }

    public BoxFile.Info uploadFileToBox(String token, FileDataRequest fileDataRequest, BodyPart filePart) throws IOException {
        // Get the desired upload path from the FileDataRequest, and set to the root folder on Box if not specified
        String uploadPath = StringUtils.isNotEmpty(fileDataRequest.getResourcePath()) ? fileDataRequest.getResourcePath() : "0";

        // Get the incoming file as a stream, then upload it to Box into the specified folder
        try (InputStream inputStream = filePart.getEntityAs(BodyPartEntity.class).getInputStream()) {
            return boxClient.getFolder(token, uploadPath)
                    .uploadFile(inputStream, filePart.getContentDisposition().getFileName());
        }
    }
}
