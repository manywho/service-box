package com.manywho.services.box.entities.actions;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.types.File;
import com.manywho.services.box.entities.types.Folder;

import javax.validation.constraints.NotNull;

public class FileCopy {
    @Property(value = "Source File", isObject = true)
    @NotNull(message = "A source file is required when copying a file")
    private File file;

    @Property(value = "Destination Folder", isObject = true)
    @NotNull(message = "A destination folder is required when copying a file")
    private Folder folder;

    @Property("Name")
    private String name;

    public File getFile() {
        return file;
    }

    public Folder getFolder() {
        return folder;
    }

    public String getName() {
        return name;
    }
}
