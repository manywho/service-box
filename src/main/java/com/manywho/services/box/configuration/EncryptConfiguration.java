package com.manywho.services.box.configuration;

import com.manywho.sdk.services.config.ServiceConfigurationProperties;

public class EncryptConfiguration extends ServiceConfigurationProperties {
    public String getInitializationInteger() {
        return this.get("encrypt.init");
    }
}
