package com.manywho.services.box.entities;

import java.util.HashMap;
import java.util.Map;

public class MetadataSearch {
    private String fileId;
    private String folderId;
    private Map<String, String> fields = new HashMap<>();

    public String getFileId() {
        return fileId;
    }

    public MetadataSearch setFileId(String fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getFolderId() {
        return folderId;
    }

    public MetadataSearch setFolderId(String folderId) {
        this.folderId = folderId;
        return this;
    }

    public MetadataSearch setFields(Map<String, String> fields) {
        this.fields = fields;
        return this;
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public MetadataSearch addField(String name, String value) {
        this.fields.put(name, value);
        return this;
    }
}
