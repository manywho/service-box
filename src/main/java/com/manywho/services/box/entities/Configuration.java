package com.manywho.services.box.entities;

import com.manywho.sdk.services.annotations.Property;

public class Configuration {
    @Property("Enterprise ID")
    private String enterpriseId;

    @Property("Verification Token")
    private String verificationToken;

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public String getVerificationToken() {
        return verificationToken;
    }
}
