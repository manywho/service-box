package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationProperties;

public class SecurityConfiguration extends ServiceConfigurationProperties {
    public String getPrivateKeyLocation() {
        return this.get("secure.privateKeyLocation");
    }

    public String getPrivateKeyPassword() {
        return this.get("secure.privateKeyPassword");
    }
}
