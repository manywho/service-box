package com.manywho.services.box.entities;

import com.manywho.sdk.services.annotations.Property;

public class Configuration {
    @Property("Enterprise ID")
    private String enterpriseId;

    public String getEnterpriseId() {
        return enterpriseId;
    }
}
