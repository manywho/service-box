package com.manywho.services.box.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.Folder;

import javax.validation.constraints.NotNull;

public class FolderCreate {
    @Property(value = "Parent Folder", isObject = true)
    @NotNull(message = "A parent folder is required when creating a new folder")
    private Folder folder;

    @Property("Name")
    @NotNull(message = "A name is required when creating a folder")
    private String name;

    public Folder getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }
}
