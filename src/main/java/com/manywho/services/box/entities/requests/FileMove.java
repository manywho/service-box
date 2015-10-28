package com.manywho.services.box.entities.requests;

import com.manywho.sdk.services.annotations.Property;
import com.manywho.services.box.entities.File;
import com.manywho.services.box.entities.Folder;

import javax.validation.constraints.NotNull;

public class FileMove {
    @Property(value = "Source File", isObject = true)
    @NotNull(message = "A source file is required when moving a file")
    private File file;

    @Property(value = "Destination Folder", isObject = true)
    @NotNull(message = "A destination folder is required when moving a file")
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
