package com.manywho.services.box.entities.actions;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.types.File;
import com.manywho.services.box.entities.types.Folder;

import javax.validation.constraints.NotNull;

public class FileCopy {
    @Property(value = "Source File ID")
    @NotNull(message = "A source file is required when copying a file")
    private String fileId;

    @Property(value = "Destination Folder ID")
    @NotNull(message = "A destination folder is required when copying a file")
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
