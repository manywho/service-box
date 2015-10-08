package com.manywho.services.box.entities;

import com.manywho.sdk.services.annotations.Id;
import com.manywho.sdk.services.annotations.Property;
import com.manywho.sdk.services.annotations.Type;

@Type(com.manywho.services.box.types.File.NAME)
public class File {
    @Id
    @Property("ID")
    private String id;

    @Property("Name")
    private String name;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
