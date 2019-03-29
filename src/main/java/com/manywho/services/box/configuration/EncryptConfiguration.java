package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationProperties;

public class EncryptConfiguration extends ServiceConfigurationProperties {
    public String getVerificationKey() {
        return this.get("verification.key");
    }

    public String getTestValue() {
        return this.get("test.value");
    }
}
