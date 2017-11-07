package com.manywho.services.box.entities.actions;

import com.manywho.sdk.services.annotations.Property;
import javax.validation.constraints.NotNull;

public class FileMove {
    @Property(value = "Source File ID")
    @NotNull(message = "A source file is required when moving a file")
    private String fileId;

    @Property(value = "Destination Folder ID")
    @NotNull(message = "A destination folder is required when moving a file")
    private String folderId;

    @Property("Name")
    private String name;

    public String getFileId() {
        return fileId;
    }

    public String getFolderId() {
        return folderId;
    }

    public String getName() {
        return name;
    }
}
